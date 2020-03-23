对于链表的学习
java中的LinkedList底层由双向链表实现，和ArrayList一样支持空值和重复值
由于底层基于链表实现，所以不能像ArrayList那样扩容（想起了arraylist的扩容机制，在复习一边）
基于实现原理的，LinkedList需要一部分存储空间来存储前驱后继关系，但是它在表头表尾实现增删效率比较高（在中间效率一般，因为需要遍历来定位）
最后，LinkedList是非线程安全的集合类

参考至https://blog.csdn.net/ThinkWon/article/details/102573923
