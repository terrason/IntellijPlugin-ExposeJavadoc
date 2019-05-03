ExposeJavadoc
=====================

An Intellij IDEA plugin for enabling `$field.javadoc` 
in the template script when Generating Getter or Setter.

编辑Getter或Setter模版时，Intellij IDEA 默认无法获取对应变量上的JavaDoc文档，
ExposeJavadoc插给你最需要的`$field.javadoc`。

你可以在模版头部插入：
```
#if($field.javadoc)
$field.javadoc ##
#end

```
就可以在生成Getter或Setter方法时附带上对应属性的JavaDoc。