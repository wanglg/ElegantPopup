# 背景

首先，在使用弹窗的时候你是否遇见过以下问题

- 弹窗类型很多，使用方法各不相同
- 弹窗动画自定义困难，要写很多代码
- 弹窗影响了虚拟按键颜色和状态栏颜色，体验较差
- 全屏弹窗不够兼容
- 弹窗自定义位置麻烦
- 相对具体view弹气泡总要在布局里增加默认隐藏的layout
- 弹窗没有很好的适配软键盘
- 不做处理，很容易弹出多个重复弹窗
- 弹窗混乱，屏幕一下显示了很多弹窗

如果这些问题困扰过你，这个库可以给你一个方案

#  设计思路

本库基础代码来源于[XPopup](https://github.com/li-xiaojun/XPopup) 感谢作者，在此基础上做了一些设计上的小改动

XPopup库只支持view的弹框，这样在使用的时候，有一定的局限性，比如可能我在dialog上使用的话，层级会在dialog下边，所有ElegantPopup支持dialog和view两种方式的弹窗，具体实现只要修改继承关系就可以低成本切换

基础View弹窗场景和类型如下

1. Center类型，就是在中间弹出的弹窗，比如确认和取消弹窗，Loading弹窗
2. Bottom类型，就是从页面底部弹出，比如从底部弹出的分享窗体，知乎的从底部弹出的评论列表，我内部会处理好手势拖拽和嵌套滚动
3. Attach类型，就是弹窗的位置需要依附于某个View或者某个触摸点，就像系统的PopupMenu效果一样，但PopupMenu的自定义性很差，淘宝的商品列表筛选的下拉弹窗也属于这种，微信的朋友圈点赞弹窗也是这种。
4. DrawerLayout类型，就是从窗体的坐边或者右边弹出，并支持手势拖拽；好处是与界面解耦，可以在任何界面显示DrawerLayout
5. 全屏弹窗，弹窗是全屏的，就像Activity那样，可以设置任意的动画器；适合用来实现登录，选择性的界面效果。
6. 自由定位弹窗(Position)，弹窗是自由的，你可放在屏幕左上角，右下角，或者任意地方，结合强大的动画器，可以实现各种效果。

基础dialog支持

1. Center类型 普通弹框
2. BottomSheet 弹框
3. 全屏弹框

# 快速开始

添加依赖

```
implementation 'com.leo.uilib:elegant-popup:0.0.2'
```

需支持androidx和Java8支持

简单使用场景

1. 普通对话框

   ```
    new Popup.Builder(getContext())
                     .asConfirm(
                      "我是标题",
                      "我是内容。",
                       "取消",
                       "确定"
                       , null,
                        false)
                        .showPopup();
   ```

2 . 自定义对话框

```
new Popup.Builder(getContext())
                        .asCustom(new ...)
                        .showPopup();
```

具体参数作用详见Demo

# ScreenShot

![image](https://github.com/wanglg/resource/blob/master/11587982016_.pic.jpg?raw=true)

