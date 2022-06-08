package commonUtility.ui;

import aiconnector.connector.AIConnector;
import aiconnector.connector.AIRectangle;
import aiconnector.manager.AILayerManager;
import aiconnector.manager.AIManagerItf;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.function.TriFunction;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 数据流程图控件容器
 */
public class DataFlowPane extends AnchorPane {

    @Getter
    private Pane canvas;
    /**
     * 代表一个一个的矩形
     */
    private final ConcurrentHashMap<Integer, Node> entities = new ConcurrentHashMap<>();
    /**
     * 代表矩形和矩形之间的关系
     */
    private final ConcurrentHashMap<Node,List<Node>> entityRelation = new ConcurrentHashMap<>();
    /**
     * 与矩形相关的连线
     */
    private final ConcurrentHashMap<Node, List<Group>> entityConnection = new ConcurrentHashMap<>();
    /**
     * connectorID->Label
     */
    private final ConcurrentHashMap<Integer, Label> connectionLabel = new ConcurrentHashMap<>();
    /**
     * connectionID -> (Src,Dst)
     */
    private final ConcurrentHashMap<Integer, Pair<Region,Region>> connectionNodes = new ConcurrentHashMap<>();
    /**
     * 代表一个一个的group区域,String为该区域名称（group区域的大小即为组框的大小）。
     */
    private final ConcurrentHashMap<String, Group> groups = new ConcurrentHashMap<>();

    /**
     * 代表group与其他group之间存在关系
     */
    private final ConcurrentHashMap<String,List<String>> groupRelation = new ConcurrentHashMap<>();
    /**
     * group之间的连线
     */
    private final ConcurrentHashMap<String,List<Group>> groupConnection = new ConcurrentHashMap<>();
    /**
     * 代表group的当前状态：缩略图=false、正常图=true.
     */
    private final ConcurrentHashMap<String, Boolean> groupStatus = new ConcurrentHashMap<>();
    /**
     * 背景和group组对象
     */
    private final ConcurrentHashMap<Group,AnchorPane> groupBackGround = new ConcurrentHashMap<>();
    /**
     * AIManager路径搜索
     */
    private final AIManagerItf aiManagerItf = AILayerManager.getInstance().getManager(this.toString());
    private final double padding =20;
    /**
     * 是否显示标签文本,默认显示
     */
    @Setter
    private boolean showLabel = true;
    /**
     * canvas内部事件回调
     */
    TriFunction<MouseEvent, Node, Point2D, Boolean> eventCallback = (mouseEvent, node, point2D) -> false;

    /**
     * 获取下一个可用的group名称
     * @return group名
     */
    public final String getNextGroupName() {
        String defaultName = "group #";
        int size = groups.size();
        String nextName = defaultName + size;
        while (groups.containsKey(nextName)) {
            size = size +1;
            nextName = defaultName + size;
        }
        return nextName;
    }

    public DataFlowPane() {
        init();
    }

    /**
     * 初始化。<p>
     *     1、绘制背景；2、添加事件
     */
    private void init() {
        double prefWidth = this.getPrefWidth();
        double prefHeight = this.getPrefHeight();
        Pair<ScrollPane, Pane> pivotZoom = PivotZoom.createPivotZoom(3508,2479,this::onEventInCanvas);
        ScrollPane value0 = pivotZoom.getValue0();
        canvas = pivotZoom.getValue1();
        this.getChildren().add(value0);
        AnchorPane.setTopAnchor(value0,0.);
        AnchorPane.setBottomAnchor(value0,0.);
        AnchorPane.setLeftAnchor(value0,0.);
        AnchorPane.setRightAnchor(value0,0.);

        DragListener.enableDrop(canvas, canvas);
    }

    /**
     * 处理来自绘图层的事件
     * @return
     */
    public boolean onEventInCanvas(MouseEvent mouseEvent, Node node, Point2D point2D) {
        return eventCallback.apply(mouseEvent, node, point2D);
    }

    /**
     * 注册canvas内部事件处理
     * @param eventCallback
     */
    public void registerEventInCanvas(TriFunction<MouseEvent, Node, Point2D, Boolean> eventCallback) {
        this.eventCallback = eventCallback;
    }
    /**
     * 向指定点添加node对象.
     * @param node
     * @param layoutX
     * @param layoutY
     */
    public void addEntity(Region node, long layoutX, long layoutY) {
        addEntity( node, layoutX, layoutY, node.hashCode());
    }
    public void addEntity(Region node, long layoutX, long layoutY, int nodeID) {
        // 内存添加 entity
        entities.put(nodeID,node);
        // 界面添加
        if (!canvas.getChildren().contains(node)) {
            canvas.getChildren().add(node);
        }
        node.setLayoutX(layoutX);
        node.setLayoutY(layoutY);

        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                onEntityMoved(node, false);
            }
        });
    }

    /**
     * 图元移动以后修改位置
     * @param node
     * @param groupMoved 是否是组移动引起的图元移动
     */
    private void onEntityMoved(Region node, boolean groupMoved) {
        int hashCodeForm = node.hashCode();

        Point position = new Point((int) node.getLayoutX(), (int) node.getLayoutY());
        if (node.getParent() instanceof Group group) {
            position = transformToRelativePosition(node, group, true);
        }
        AIRectangle newRect = new AIRectangle(position.x, position.y, (int) node.getWidth(), (int) node.getHeight(), hashCodeForm);

        // 保存现场
        List<AIConnector> connectors = null;
        CopyOnWriteArrayList<Integer> connectorsList = aiManagerItf.get_connections(hashCodeForm);
        if (connectorsList != null) {
            connectors = connectorsList.parallelStream().map(integer -> {
                AIConnector aiConnector = aiManagerItf.get_connection(integer);
                AIRectangle srcRect = aiConnector.get_srcRect();
                AIRectangle dstRect = aiConnector.get_dstRect();
                srcRect = srcRect.get_table_id() == hashCodeForm? newRect: srcRect;
                dstRect = dstRect.get_table_id() == hashCodeForm? newRect: dstRect;

                return new AIConnector(srcRect, dstRect, aiConnector.get_connector_id(), aiManagerItf);
            }).toList();
        }

        // 重建内存模型：移动图元
        aiManagerItf.move_rect(hashCodeForm, newRect);
        // 目前没有建立连线，直接返回.
        if (connectors == null) {
            return;
        }

        // 界面处理: 重新搜索路径
        connectors.forEach(newConnector -> {
            aiManagerItf.add_line(newConnector);

            List<Point> route = newConnector.search_route();
            int connector_id = newConnector.get_connector_id();
            Pair<Region, Region> nodePair = connectionNodes.get(connector_id);
            Region srcNode = nodePair.getValue0();
            Region dstNode = nodePair.getValue1();
            Label label = connectionLabel.get(connector_id);
            doDrawRoute(srcNode, dstNode, route, label.getText(), connector_id);
        });

    }

    /**
     * 从画布中删除,如果删除后组为空，删除组
     * @param node 待删除对象
     * @return true：删除；false: 未删除或不存在
     */
    public boolean removeEntity(Region node) {
        Node remove = entities.remove(node.hashCode());
        if (remove == null) {
            return false;
        }

        // 内存：删除与其相关的连线、关联关系
        List<Group> groupLine = entityConnection.remove(node);
        List<Node> nodesRelation = entityRelation.remove(node);
        nodesRelation.forEach( r -> {
            entityConnection.getOrDefault(r,new ArrayList<>()).removeAll(groupLine);
            entityRelation.getOrDefault(r, new ArrayList<>()).remove(node);
        });

        // 界面：对象、连线删除(group删除、canvas删除都执行，有则删没有不影响)
        Parent parent = node.getParent();
        // 是否已经形成 group，形成从group中删除（删除后如果组内为空那么删除组），否则从画布删除
        if (parent instanceof Group group) {
            group.getChildren().remove(node);
            group.getChildren().removeAll(groupLine);

            if(group.getChildren().isEmpty()) {
                removeGroup(group);
            }
        }

        // 界面：删除界面上的未分组部分连线
        canvas.getChildren().remove(node);
        canvas.getChildren().removeAll(groupLine);

        return true;
    }

    /**
     * 删除背景组对象（删除对象及与其他组的联系）,不删除组对象
     * @param groupName 待删除组对象
     */
    public void removeGroup(String groupName) {
        Group group = groups.get(groupName);
        if (group != null) {
            removeGroup(group);
        }
    }

    /**
     * 删除背景组对象（删除对象及与其他组的联系）,不删除组对象
     * @param group 待删除组对象
     */
    public void removeGroup(Group group) {
        Parent groupBK = group.getParent();

        // 内存：对象
        groups.remove(group.getId());
        groupBackGround.remove(group);
        // 内存：关系
        List<Group> removeGroupConnection = groupConnection.remove(group.getId());
        if (removeGroupConnection !=null) {
            removeGroupConnection.forEach(rel -> groupConnection.getOrDefault(rel.getId(),new ArrayList<>()).removeAll(removeGroupConnection));
        }
        List<String> removeGroup = groupRelation.remove(group.getId());
        if (removeGroup != null) {
            removeGroup.forEach(rel -> groupRelation.getOrDefault(rel, new ArrayList<>()).remove(rel));
        }

        // 界面：将组内对象添加到 绘图层
        List<Node> nodes = group.getChildren().stream().toList();
        List<Pair<Node, Point>> relativePositions = nodes.stream().map(node -> Pair.with(node,transformToRelativePosition(node, group, true))).toList();
        relativePositions.forEach( objects -> {
            Node node = objects.getValue0();
            canvas.getChildren().add(node);
            node.setLayoutX(objects.getValue1().getX());
            node.setLayoutY(objects.getValue1().getY());
        });

        // 界面：删除父类背景
        canvas.getChildren().remove(groupBK);
    }



    /**
     * 搜索路径
     * @param srcNode 源图元
     * @param dstNode 目标图元
     * @return <路径坐标，路径ID>
     */
    private Pair<List<Point>, Integer> searchRoute(Region srcNode, Region dstNode) {
        return searchRoute(srcNode, dstNode, Integer.MAX_VALUE);
    }

    /**
     * 重新搜索路径
     * @param srcNode 源图元
     * @param dstNode 目标图元
     * @return <路径坐标，路径ID>
     */
    private Pair<List<Point>, Integer> searchRoute(Region srcNode, Region dstNode, int hashCodeLine){
        // 查找AI中对应矩形，不存在则添加
        Optional<AIRectangle> srcRect = aiManagerItf.find_rect(srcNode.hashCode());
        Optional<AIRectangle> dstRect = aiManagerItf.find_rect(dstNode.hashCode());
        if (srcRect.isEmpty()) {
            Point position = new Point((int) srcNode.getLayoutX(), (int) srcNode.getLayoutY());
            if (srcNode.getParent() instanceof Group group) {
                position = transformToRelativePosition(srcNode, group, true);
            }
            aiManagerItf.add_rect(new AIRectangle(position.x, position.y, (int) srcNode.getWidth(), (int) srcNode.getHeight(), srcNode.hashCode()));
        }
        if (dstRect.isEmpty()) {
            Point position = new Point((int) dstNode.getLayoutX(), (int) dstNode.getLayoutY());
            if (dstNode.getParent() instanceof Group group) {
                position = transformToRelativePosition(dstNode, group, true);
            }
            aiManagerItf.add_rect(new AIRectangle(position.x,position.y, (int) dstNode.getWidth(), (int) dstNode.getHeight(), dstNode.hashCode()));
        }
        srcRect = aiManagerItf.find_rect(srcNode.hashCode());
        dstRect = aiManagerItf.find_rect(dstNode.hashCode());

        // 内存连线初始化: 添加连线
        AIConnector connector = aiManagerItf.get_connection(hashCodeLine);
        if (connector == null) {
            connector = new AIConnector(srcRect.get(), dstRect.get(), aiManagerItf);
            aiManagerItf.add_line(connector);
            hashCodeLine = connector.get_connector_id();
        }

        return Pair.with(connector.search_route(), hashCodeLine);
    }

    /**
     * 添加两个node之间的关联关系.
     * @param srcNode 源
     * @param dstNode 目标
     * @return Group 路径组
     */
    public Group addEntityRelation(Region srcNode, Region dstNode, @Nullable String labelName) {
        // 路径自动搜索
        Pair<List<Point>, Integer> routeIdPair = searchRoute(srcNode, dstNode);
        List<Point> route1 = routeIdPair.getValue0();
        int id = routeIdPair.getValue1();
        if (route1 == null) {
            return null;
        }
        return addEntityRelation(srcNode, dstNode, route1, labelName, id);
    }

    /**
     *
     * 添加两个node之间的关联关系，同一个routeId支持多次调用
     * @param srcNode 源
     * @param dstNode 目标
     * @param route 路径坐标点
     * @param labelName 文本提示
     * @param connectionID connectionID
     * @return 路径组
     */
    private Group addEntityRelation(Region srcNode, Region dstNode, List<Point> route, String labelName, int connectionID) {
        return addEntityRelation(srcNode, dstNode, route, labelName, Color.PINK, connectionID);
    }
    private Group addEntityRelation(Region srcNode, Region dstNode, List<Point> route, String labelName, Color color, int connectionID) {

        connectionNodes.put(connectionID, Pair.with(srcNode,dstNode));
        // 添加 entity-> entity关系
        List<Node> nodeList = entityRelation.computeIfAbsent(srcNode, key -> new ArrayList<>());
        if (!nodeList.contains(dstNode)) nodeList.add(dstNode);
        List<Node> nodeList1 = entityRelation.computeIfAbsent(dstNode, key -> new ArrayList<>());
        if (!nodeList1.contains(srcNode)) nodeList1.add(srcNode);

        // 画线
        Group groupRoute = doDrawRoute(srcNode, dstNode, route, labelName, connectionID);

        // 添加entity->connection关联关系
        List<Group> listConnectionSrc = entityConnection.computeIfAbsent(srcNode, key -> new ArrayList<>());
        if (!listConnectionSrc.contains(groupRoute)) listConnectionSrc.add(groupRoute);
        List<Group> listConnectionDst = entityConnection.computeIfAbsent(dstNode, key -> new ArrayList<>());
        if (!listConnectionDst.contains(groupRoute))listConnectionDst.add(groupRoute);

        return groupRoute;
    }

    /**
     * 指定connectionID进行画线，该connectionID必须为已存在的连线ID。如果是组内对象连线，将连线添加到组.
     * @param srcNode 源图元
     * @param dstNode 目标图元
     * @param route 路径
     * @param labelName 文本
     * @param connectionID 连线ID
     * @return 路径组
     */
    @NotNull
    private Group doDrawRoute(Region srcNode, Region dstNode, List<Point> route, String labelName, int connectionID) {
        Label label = null;
        if (showLabel) {
            label = connectionLabel.computeIfAbsent(connectionID, key -> new Label(labelName));
        }
        List<Group> connections = entityConnection.computeIfAbsent(srcNode, key -> new ArrayList<>());
        Optional<Group> groupOptional = connections.stream().filter(group -> group.getId().equals(String.valueOf(connectionID))).findFirst();
        Group groupRoute = doDrawRoute(route, label, groupOptional.orElse(new Group()));
        groupRoute.setId(String.valueOf(connectionID));

        return groupRoute;
    }

    /**
     * 删除连线(entity关联关系也会删除)
     * @param groupLine 待删除连线
     */
    public void removeEntityRelation(Group groupLine) {
        // 内存：删除与其相关的连线
        List<Pair<Node, List<Group>>> toList = entityConnection.entrySet().parallelStream().filter(entry -> {
            List<Group> groupList = entry.getValue();
            return groupList.contains(groupLine);
        }).map(entry -> {
            List<Group> groupList = entry.getValue();
            groupList.remove(groupLine);
            return Pair.with(entry.getKey(), groupList);
        }).filter(p -> p.getValue1().isEmpty())
                .toList();
        // 内存：删除关联关系(连线在上一步已经删除)
        toList.forEach(objects -> {
            Node emptyConnectionNode = objects.getValue0();
            List<Node> nodesRelation = entityRelation.remove(emptyConnectionNode);
            nodesRelation.forEach( r -> {
                entityRelation.getOrDefault(r, new ArrayList<>()).remove(emptyConnectionNode);
            });
        });

        // 界面：连线删除(group删除、canvas删除都执行，有则删没有不影响)
        Parent parent = groupLine.getParent();
        // 是否已经形成 group，形成从group中删除（删除后如果组内为空那么删除组），否则从画布删除
        if (parent instanceof Group group) {
            group.getChildren().removeAll(groupLine);
        }
        canvas.getChildren().removeAll(groupLine);
    }

    /**
     * 将图元添加到组，可以添加一个或多个图元
     * @param srcNode
     * @param dstNode
     * @param color
     * @param label
     * @param groupRoute
     * @return
     */
//    public Group addToGroup(Node srcNode, Node dstNode, Color color, Label label, Group groupRoute) {
//        // 处理group关系:1、如果没有group，形成group；2、如果分别位于两个group，那么合并group; 3、如果其中一个已在group中，那么添加到现有group；
//        Group toGroup = null;
//        Group groupSrcNode = getGroup(srcNode);
//        Group groupDstNode = getGroup(dstNode);
//        if (groupSrcNode == null && groupDstNode == null) {
//            toGroup = addToGroup(srcNode);
//            canvas.getChildren().add(toGroup);
//        } else if (groupSrcNode !=null && groupDstNode != null) {
//            toGroup = groupSrcNode;
//        } else {
//            toGroup = groupSrcNode !=null? groupSrcNode: groupDstNode;
//        }
//
//        addToGroup(srcNode, toGroup);
//        addToGroup(dstNode, toGroup);
//        addToGroup(groupRoute,toGroup);
//        if (label != null) addToGroup(label, toGroup);
//
//        Region background = drawBackground(color, toGroup);
//        groupRegion.put(toGroup,background);
//
//        return toGroup;
//    }

    /**
     * 画背景框图:1、group内的对象发生变化时重新画背景
     * @param color 背景框图的颜色
     * @param toGroup 背景框图包含的组对象(以组对象大小画框)
     * @return
     */
    private AnchorPane drawBackground(Color color, Group toGroup) {
        // 画组背景框图/更新背景框图
        assert toGroup != null;
        Bounds bounds = toGroup.getLayoutBounds();
        // 添加背景框图
        AnchorPane pane = groupBackGround.computeIfAbsent(toGroup, key ->{ AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(toGroup);
            AnchorPane.setLeftAnchor(toGroup,padding);
            AnchorPane.setTopAnchor(toGroup,padding);
            canvas.getChildren().add(anchorPane);
            anchorPane.setOnMouseDragged(mouseEvent -> {
                List<Node> children = toGroup.getChildren().filtered(entities::contains).stream().toList();
                children.forEach(node -> {
                    if (entities.contains(node)) {
                        onEntityMoved((Region) node, true);
                    }
                });
            });
            anchorPane.toBack();
            return anchorPane;
        });

        // 设置背景框图位置
        pane.setLayoutX(bounds.getMinX()- padding);
        pane.setLayoutY(bounds.getMinY()- padding);
        pane.setPrefWidth(bounds.getWidth()+ padding *2);
        pane.setPrefHeight(bounds.getHeight()+ padding *2);

        pane.setOnMouseEntered(mouseEvent -> pane.setCursor(Cursor.MOVE));
        pane.setOnMouseExited(mouseEvent -> pane.setCursor(Cursor.DEFAULT));

        if (color != null) pane.setBackground(new Background(new BackgroundFill(color,null,null)));

        return pane;
    }

    /**
     * 根据 route 画线
     * @param route 路径
     * @param label 线文字标识.当 =null时不显示
     * @return 路径group组
     */
    @NotNull
    private Group doDrawRoute(List<Point> route, Label label, Group group) {
        group.getChildren().clear();
        Point prevPoint = null;
        boolean labelPositioned = false;    // 是否已设置label位置
        for (Point point: route) {
            if (prevPoint == null) {
                prevPoint = point;
                continue;
            }
            // 设置连线位置
            HBox box = new HBox(new Line(prevPoint.getX(), prevPoint.getY(),point.getX(), point.getY()));
            if (prevPoint.getX() < point.getX()) {
                box.setLayoutX(prevPoint.getX());
                box.setLayoutY(prevPoint.getY());
            } else if (prevPoint.getX() > point.getX()) {
                box.setLayoutX(point.getX());
                box.setLayoutY(point.getY());
            } else if (prevPoint.getY() < point.getY()) {
                box.setLayoutX(prevPoint.getX());
                box.setLayoutY(prevPoint.getY());
            } else if (prevPoint.getY() > point.getY()) {
                box.setLayoutX(point.getX());
                box.setLayoutY(point.getY());
            }
            // 根据box位置设置label位置
            if (prevPoint.getX() != point.getX()) {
                box.setStyle("-fx-border-width: 3");
//                box.setPadding(new Insets(3,0,3,0));
                if (!labelPositioned && label != null) {
                    label.setLayoutX(box.getLayoutX());
                    double CONSTANT_lineTipUpPosition = 12;
                    label.setLayoutY(box.getLayoutY()- CONSTANT_lineTipUpPosition);
                    labelPositioned = true;
                }
            }else if (prevPoint.getY() != point.getY()) {
//                box.setPadding(new Insets(0,3,0,3));
                if (!labelPositioned && label != null) {
                    label.setRotate(90);
                    label.setLayoutX(box.getLayoutX()- label.getText().length()*3.5);
                    label.setLayoutY(box.getLayoutY()+ label.getText().length()*3.5);
                    labelPositioned = true;
                }
            }
            group.getChildren().add(box);
            prevPoint = point;
        }
        if (!canvas.getChildren().contains(group)) canvas.getChildren().add(group);
        if (label != null && !canvas.getChildren().contains(label) && showLabel) canvas.getChildren().add(label);
        return group;
    }

    /**
     * 获取node对应的group。如果node没有直接group，那么找与其相关联的node的group
     * @param node
     * @return
     */
    private Group getGroup(Node node) {
        Parent parent = node.getParent();
        if (parent instanceof Group) {
            return (Group)parent;
        }
        return null;
    }

    /**
     * 添加到现有或新建分组
     * @param nodes 节点
     * @param groupName 分组名称
     * @return
     */
    public Group addToGroup(String groupName, Node... nodes) {
        return addToGroup(groups.computeIfAbsent(groupName, key->{
            return addGroup(groupName);
        }), nodes);
    }

    @NotNull
    private Group addGroup(String groupName) {
        Group g = new Group();
        g.setId(groupName);
        canvas.getChildren().add(g);
        return g;
    }

    private Group addToGroup(Group group, Node... nodes) {

        // 添加节点到分组
        for (Node node: nodes) {
            if (group.getChildren().contains(node)) continue;
            group.getChildren().add(node);
            moveToRelativePosition(node, group, false);
        }

        // 添加连线到分组：将与现有分组中节点有关系的连线也加入分组（与分组中节点没关系的连线不加入）
        List<Group> validConnections = group.getChildren().parallelStream()
                .flatMap((Function<Node, Stream<Group>>) node -> entityConnection.getOrDefault(node, new ArrayList<>()).stream())
                .filter(group12 -> {
                    String connectionID = group12.getId();
                    Pair<Region, Region> objects = connectionNodes.get(Integer.parseInt(connectionID));
                    return objects != null && group.getChildren().contains(objects.getValue0()) && group.getChildren().contains(objects.getValue1());
                }).toList();

        validConnections.forEach(group1 -> { if(!group.getChildren().contains(group1)) group.getChildren().addAll(group1);});
        // 添加标签文本到分组
        if(showLabel) {
            validConnections.forEach(group1 -> {
                Label label = connectionLabel.get(Integer.parseInt(group1.getId()));
                if(!group.getChildren().contains(label)) {
                    group.getChildren().add(label);
                }
            });
        }

        AnchorPane background = drawBackground(Color.PINK, group);

        return group;
    }

    /**
     * 1、将node由绝对位置移动到group相对位置；
     * 2、将node由group相对位置移动到绝对位置;
     * @param node
     * @param group
     * @param relativeOrGlobal 转换为相对位置还是决定位置.true: 绝对位置；false:相对位置.
     */
    private void moveToRelativePosition(Node node, Node group, boolean relativeOrGlobal) {
        Point point = transformToRelativePosition(node, group, relativeOrGlobal);
        node.setLayoutX(point.getX());
        node.setLayoutY(point.getY());
    }
    /**
     * 1、将node由绝对位置转换为group相对位置；
     * 2、将node由group相对位置转换为绝对位置;
     * @param node
     * @param group
     * @param relativeOrGlobal 转换为相对位置还是决定位置. true: 绝对位置；false:相对位置.
     */
    private Point transformToRelativePosition(Node node, Node group, boolean relativeOrGlobal) {
        double layoutX = node.getLayoutX();
        double layoutY = node.getLayoutY();
        double groupLayoutX = group.getLayoutX();
        double groupLayoutY = group.getLayoutY();
        Parent rectangle = group.getParent();
        double rectangleLayoutX = rectangle.getLayoutX();
        double rectangleLayoutY = rectangle.getLayoutY();

        double deltaX;
        double deltaY;
        if (relativeOrGlobal) {
            deltaX = layoutX + groupLayoutX + rectangleLayoutX;
            deltaY = layoutY + groupLayoutY + rectangleLayoutY;
        } else {
            deltaX = layoutX - groupLayoutX - rectangleLayoutX;
            deltaY = layoutY - groupLayoutY - rectangleLayoutY;
        }

        return new Point((int) deltaX, (int) deltaY);
    }

    /**
     * 获取间接group
     * @param node
     * @return
     */
    private Group getIndirectGroup(Node node) {
//        for (Map.Entry<Pair<Node, Node>, Group> entry: entityRelation.entrySet()) {
//            Pair<Node, Node> entryKey = entry.getKey();
//            Node keyValue0 = entryKey.getValue0();
//            Node keyValue1 = entryKey.getValue1();
//            Node relNode = keyValue0.equals(node)? keyValue1:keyValue0;
//            Parent parent = relNode.getParent();
//            if (parent instanceof Group) {
//                return (Group)parent;
//            }
//        }
        return null;
    }


}
