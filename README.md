[TOC]

#PathAnimation
tag:  Android  PathAnimation

`PathAnimation`  路径动画是指将视图节点沿着指定路径执行动画

***

###Android 5.1 以后对`PathAnimation`的支持

在 **API 21** 之后`ObjectAnimator` 添加了 **Path animation** 的支持，下面是 **API 21** 中 **PathAnimation Demo **的完整路径

> ~/sdk/samples/android-22/legacy/ \
> ApiDemos/src/com/example/android/apis/animation/PathAnimations.java

 我们来看一下这个demo中，`ObjectAnimator` 提供的哪些方法来实现路径动画

***

``` java
	ObjectAnimator.ofFloat(view, "x", "y", path);
```
这个 `Animator` 的每一帧动画都会重新计算 `path` 对应时间点的 `position`,
然后调用 `view` 的 `setX()` 和 `setY()` 方法来更新 `view` 的位置

***

``` java
	ObjectAnimator.ofFloat(view, View.X, View.Y, path);
```
同上，但是传入的的参数换成了 `View.X` 和 `View.Y`，这两个对象都是 `Properties`  类在 `View` 类中的声明的静态对象

***

``` java
	// view中需要实现setCoordinates(int x,int y)这个方法
	ObjectAnimator.ofMultiInt(view, "setCoordinates", path); 
```

这个 `Animator` 的每一帧动画会返回两个 `int` 值 (某一时间节点对应的 `position` )，然后调用 `setCoordinates` 方法改变视图节点的位置

***

```
	// view中需要实现setCoordinatesF(float x,float y)这个方法
	ObjectAnimator.ofMultiFloat(view, "setCoordinatesF", path); 
```
同上，不过得到的值是 `Float` 类型的，计算每一帧节点位置会更精确

***

```
	// view中需要实现setPoint(PointF point)这个方法
	ObjectAnimator.ofObject(view, "point", null, path);
```
`Animator` 的每帧动画都会重新设置 `PointF` 的值

***

```
	ObjectAnimator.ofObject(view, POINT_PROPERTY, new PointFToPointConverter(), path);
```
这个方式的实现略繁琐，可以看下详细代码

***

###Android 5.1 之前该如何实现`PathAnimation`

旧版 **SDK** 并没有提供丰富的 **API** 供我们调用，分析上面所讲各个实现方法，都是按照每一帧重新计算某一时间节点对应   `path` 的 `position`，万变不离其宗，看来只要知道如何计算 `path` 的位置就可以搞定了


这里我们需要知道两个类的使用：
* 监听Animator  `AnimatorUpdateListener`
```java
ValueAnimator aimator = ValueAnimator.ofFloat(0, 1f);
aimator.setDuration(250);
aimator.addUpdateListener( new AnimatorUpdateListener() {
     @Override
     public void onAnimationUpdate(ValueAnimator animation) {
          float interpolatedTime = (Float) animation.getAnimatedValue();
     }
});
```



*  Path 测量 `PathMeasure`
```java
PathMeasure pathMeasure = new PathMeasure(path,false);
// positon 记录 x,y 的坐标
float[] position = new float[2];
pathMeasure(pathMeasure.getLength() * interpolatedTime, position, null);
```

1. `AnimatorUpdateListener`   监听每一帧时间节点的 `interpolatedTime`
2. `PathMeasure`   通过 `interpolatedTime`  计算时间节点对应的`position`
有了`position`，就可以改变每一帧视图节点所在的位置了


附赠所有方法的实现demo ，戳这里[github][1]
[1]:https://github.com/ryfthink
