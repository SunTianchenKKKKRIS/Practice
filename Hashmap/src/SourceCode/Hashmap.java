package SourceCode;

public class Hashmap {

    /**
     * 在JDK1.8之前，HashMap采用数组+链表实现，即使用链表处理冲突，同一hash值的节点都存储在一个链表里。但是当位于一个桶中的元素较多，
     * 即hash值相等的元素较多时，通过key值依次查找的效率较低。而JDK1.8中，为了解决hash碰撞过于频繁的问题，HashMap采用数组+链表+红黑树实现，
     * 当链表长度超过阈值（8）时，将链表(查询时间复杂度为O(n))转换为红黑树(时间复杂度为O(lg n))，极大的提高了查询效率。
     * 以下没有特别说明的均为JDK1.8中的HashMap。
     */

    /**
     * Hashmap是我们用的最多的map集合  它的特点有
     * 键不可重复 值可以重复（hash碰撞）
     * 底层哈希表
     * 线程不安全
     * key 和 value都允许为null
     */

    /**
     * 在一开始hashmap利用数组和链表解决hash冲突的问题  称之为拉链法
     * 具体实现就是创建一个以链表为元素的数组 当发生hash冲突时就将冲突元素加入当前key对应的链表中
     * 在jdk1.8之后  变成了使用红黑树  当链表长度大于阈值（默认8）时将链表转换为红黑树减少时间消耗（因为链表查询效率低）
     */

    /**
     * java 8 中解决优化的问题
     * resize 扩容优化
     * 引入了红黑树，目的是避免单条链表过长而影响查询效率
     * 解决了多线程死循环问题，但仍是非线程安全的，多线程时可能会造成数据丢失问题
     */


    /**
     * 成员变量
     */

    //默认初始化Node数组容量16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    //最大的数组容量
    static final int MAXIMUM_CAPACITY = 1 << 30;
    //默认负载因子0.75
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    //由链表转红黑树的临界值
    static final int TREEIFY_THRESHOLD = 8;
    //由红黑树转链表的临界值
    static final int UNTREEIFY_THRESHOLD = 6;
    //桶转化为树形结构的最小容量
    static final int MIN_TREEIFY_CAPACITY = 64;
    //HashMap结构修改的次数，结构修改是指更改HashMap中的映射数或以其他方式修改其内部结构(例如，rehash的修改)。
    // 该字段用于在Collection-views上快速生成迭代器。
    transient int modCount;
    //Node数组下一次扩容的临界值，第一次为16*0.75=12（容量*负载因子）
    int threshold;
    //负载因子
    final float loadFactor;
    //map中包含的键值对的数量
    transient int size;
    //表数据，即Node键值对数组，Node是单向链表，它实现了Map.Entry接口，总是2的幂次倍

//Node<K,V>是HashMap的内部类，实现Map.Entry<K,V>接口，HashMap的哈希桶数组中存放的键值对对象就是Node<K,V>。
// 类中维护了一个next指针指向链表中的下一个元素。值得注意的是，当链表中的元素数量超过TREEIFY_THRESHOLD后会HashMap会将链表转换为红黑树，
// 此时该下标的元素将成为TreeNode<K,V>,继承于LinkedHashMap.Entry<K,V>，而LinkedHashMap.Entry<K,V>是Node<K,V>的子类，
// 因此HashMap的底层数组数据类型即为Node<K,V>。
    transient Node<K,V>[] table;
    //存放具体元素的集,可用于遍历map集合
    transient Set<Map.Entry<K,V>> entrySet;

    /**
     * capacity、threshold和loadFactor之间的关系
     * 一般 threshold = capacity * loadFactor，默认的负载因子0.75是对空间和时间效率的一个平衡选择，建议大家不要修改。
     */

    /**
     * 构造方法
     */

    //初始化容量以及负载因子
    public HashMap(int initialCapacity, float loadFactor) {
        //判断初始化数组的容量大小
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        //判断初始化的负载因子大小和是否为浮点型
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        //初始化负载因子
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    //初始化容量
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    //默认构造方法
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }

    //把另一个Map的值映射到当前新的Map中
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
    /**
     * 当我们自定义HashMap初始容量大小时，构造函数并非直接把我们定义的数值当做HashMap容量大小，
     * 而是把该数值当做参数调用方法tableSizeFor，然后把返回值作为HashMap的初始容量大小
     */

    //HashMap 中 table 角标计算及table.length 始终为2的幂，即 2 ^ n
//返回大于initialCapacity的最小的二次幂数值
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
//这里tableSizeFor不是很好理解 参考https://www.jianshu.com/p/e33d3fa32091
    /**
     * 这个方法的功能是 输出一个大于等于cap的最小的2的n次方
     * 比如说输入5  那么2的n次方中  8是大于5且最小的 所以输出8
     * 由于int类型为32位，所有即使除符号为之外只有第一位为1的情况，也能将所有的位全部变成1，
     * 不过由于最后计算出来为int类型的最大值，此时返回n+1会导致溢出，不能返回期望的结果，
     * 这也是为什么在方法开始是要执行int n = c - 1;的原因。其次n-1也是为了防止一开始输入的参数本身就是2的倍数
     * 后面经过位移后得到的是参数的2倍
     *
     * 后面返回n+1是因为当参数为0时需要输出1，因为hashmap容量为0没有意义
     */

    /**
     * hashmap将hash，key，value，next封装到了一个静态内部类node上，实现了map.entry接口
     */
    static class Node<K,V> implements Map.Entry<K,V> {
        // 哈希值，HashMap根据该值确定记录的位置
        final int hash;
        // node的key
        final K key;
        // node的value
        V value;
        // 链表下一个节点
        Node<K,V> next;

        // 构造方法
        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        // 返回 node 对应的键
        public final K getKey()        { return key; }
        // 返回 node 对应的值
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        //作用：判断2个Entry是否相等，必须key和value都相等，才返回true
        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }
// 这里要注意 重写了hashcode方法后写了equals方法  要复习一下关于hashcode equals 引用这些东西之间的关系

    /**
     * TreeNode
     * 继承于LinkedHashMap.Entry<K,V>，而LinkedHashMap.Entry<K,V>是Node<K,V>的子类，因此HashMap的底层数组数据类型即为Node<K,V>
     */
    /**
     * 红黑树节点 实现类：继承自LinkedHashMap.Entry<K,V>类
     */
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {

        // 属性 = 父节点、左子树、右子树、删除辅助节点 + 颜色
        TreeNode<K,V> parent;
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;
        boolean red;

        // 构造函数
        TreeNode(int hash, K key, V val, Node<K,V> next) {
            super(hash, key, val, next);
        }

        // 返回当前节点的根节点
        final TreeNode<K,V> root() {
            for (TreeNode<K,V> r = this, p;;) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }
    }

    /**
     * 核心方法
     */

    /**
     * hash算法
     * 前面说过，jdk8以前使用数组+链表结合在一起实现hashmap。hashmap通过keyd的hashcode通过扰动函数处理后得到hash值，
     * 然后通过（n-1）&hash判断当前元素的存放位置（n为数组长度），如果当前位置存在元素，就判断该元素与要传入元素hash值和key是否相同
     * 如果相同就覆盖，不相同就拉链法
     *
     * 这里比较复杂  建议多看几遍  参考https://www.zhihu.com/question/20733617  这里简单写一些目前的理解
     */

    // 取key的hashCode值、高位运算、取模运算
// 在JDK1.8的实现中，优化了高位运算的算法，
// 通过hashCode()的高16位异或低16位实现的：(h = k.hashCode()) ^ (h >>> 16)，
// 主要是从速度、功效、质量来考虑的，这么做可以在数组table的length比较小的时候，
// 也能保证考虑到高低Bit都参与到Hash的计算中，同时不会有太大的开销。
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }


    /**
     * put()
     * 当我们put的时候，首先计算 key的hash值，这里调用了 hash方法，
     * hash方法实际是让key.hashCode()与key.hashCode()>>>16进行异或操作，高16bit补0，一个数和0异或不变，
     * 所以 hash 函数大概的作用就是：高16bit不变，低16bit和高16bit做了一个异或，目的是减少碰撞。按照函数注释，因为bucket数组大小是2的幂，
     * 计算下标index = (table.length - 1) & hash，如果不做 hash 处理，相当于散列生效的只有几个低 bit 位，为了减少散列的碰撞，
     * 设计者综合考虑了速度、作用、质量之后，使用高16bit和低16bit异或来简单处理减少碰撞，
     * 而且JDK8中用了复杂度 O（logn）的树结构来提升碰撞下的性能。
     */


    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    //实现Map.put和相关方法
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // 步骤①：tab为空则创建
        // table未初始化或者长度为0，进行扩容
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // 步骤②：计算index，并对null做处理
        // (n - 1) & hash 确定元素存放在哪个桶中，桶为空，新生成结点放入桶中(此时，这个结点是放在数组中)
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
            // 桶中已经存在元素
        // 当已存在时有三种情况 key值相同覆盖value key值不同存在链表 或 红黑树
        else {
            Node<K,V> e; K k;
            // 步骤③：节点key存在，直接覆盖value
            // 比较桶中第一个元素(数组中的结点)的hash值相等，key相等
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                // 将第一个元素赋值给e，用e来记录
                e = p;
                // 步骤④：判断该链为红黑树
                // hash值不相等，即key不相等；为红黑树结点
                // 如果当前元素类型为TreeNode，表示为红黑树，putTreeVal返回待存放的node, e可能为null
            else if (p instanceof TreeNode)
                // 放入树中
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
                // 步骤⑤：该链为链表
                // 为链表结点
            else {
                // 在链表最末插入结点
                for (int binCount = 0; ; ++binCount) {
                    // 到达链表的尾部

                    //判断该链表尾部指针是不是空的
                    if ((e = p.next) == null) {
                        // 在尾部插入新结点
                        p.next = newNode(hash, key, value, null);
                        //判断链表的长度是否达到转化红黑树的临界值，临界值为8
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            //链表结构转树形结构
                            treeifyBin(tab, hash);
                        // 跳出循环
                        break;
                    }
                    // 判断链表中结点的key值与插入的元素的key值是否相等
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        // 相等，跳出循环
                        break;
                    // 用于遍历桶中的链表，与前面的e = p.next组合，可以遍历链表
                    p = e;
                }
            }
            //判断当前的key已经存在的情况下，再来一个相同的hash值、key值时，返回新来的value这个值
            if (e != null) {
                // 记录e的value
                V oldValue = e.value;
                // onlyIfAbsent为false或者旧值为null
                if (!onlyIfAbsent || oldValue == null)
                    //用新值替换旧值
                    e.value = value;
                // 访问后回调
                afterNodeAccess(e);
                // 返回旧值
                return oldValue;
            }
        }
        // 结构性修改
        ++modCount;
        // 步骤⑥：超过最大容量就扩容
        // 实际大小大于阈值则扩容
        if (++size > threshold)
            resize();
        // 插入后回调
        afterNodeInsertion(evict);
        return null;
    }
    /**
     * 总结逻辑为
     * ①.判断键值对数组table[i]是否为空或为null，否则执行resize()进行扩容；
     * ②.根据键值key计算hash值得到插入的数组索引i，如果table[i]==null，直接新建节点添加，转向⑥，如果table[i]不为空，转向③；
     * ③.判断table[i]的首个元素是否和key一样，如果相同直接覆盖value，否则转向④，这里的相同指的是hashCode以及equals；
     * ④.判断table[i] 是否为treeNode，即table[i] 是否是红黑树，如果是红黑树，则直接在树中插入键值对，否则转向⑤；
     * ⑤.遍历table[i]，判断链表长度是否大于8，大于8的话把链表转换为红黑树，在红黑树中执行插入操作，否则进行链表的插入操作；遍历过程中若发现key已经存在直接覆盖value即可；
     * ⑥.插入成功后，判断实际存在的键值对数量size是否超多了最大容量threshold，如果超过，进行扩容。
     *
     */

    /**
     * resize方法
     * 在jdk8中，hashmap中的键值对大于阈值或者初始化时用resize
     * 每次扩展都是扩展两倍
     * 扩展后node对象在原位置或移动到原偏移量两倍的位置
     */
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;//oldTab指向hash桶数组
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {//如果oldCap不为空的话，就是hash桶数组不为空
            if (oldCap >= MAXIMUM_CAPACITY) {//如果大于最大容量了，就赋值为整数最大的阀值
                threshold = Integer.MAX_VALUE;
                return oldTab;//返回
            }//如果当前hash桶数组的长度在扩容后仍然小于最大容量 并且oldCap大于默认值16
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold 双倍扩容阈值threshold
        }
        // 旧的容量为0，但threshold大于零，代表有参构造有cap传入，threshold已经被初始化成最小2的n次幂
        // 直接将该值赋给新的容量
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
            // 无参构造创建的map，给出默认容量和threshold 16, 16*0.75
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        // 新的threshold = 新的cap * 0.75
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        // 计算出新的数组长度后赋给当前成员变量table
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];//新建hash桶数组
        table = newTab;//将新数组的值复制给旧的hash桶数组
        // 如果原先的数组没有初始化，那么resize的初始化工作到此结束，否则进入扩容元素重排逻辑，使其均匀的分散
        if (oldTab != null) {
            // 遍历新数组的所有桶下标
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    // 旧数组的桶下标赋给临时变量e，并且解除旧数组中的引用，否则就数组无法被GC回收
                    oldTab[j] = null;
                    // 如果e.next==null，代表桶中就一个元素，不存在链表或者红黑树
                    if (e.next == null)
                        // 用同样的hash映射算法把该元素加入新的数组
                        newTab[e.hash & (newCap - 1)] = e;
                        // 如果e是TreeNode并且e.next!=null，那么处理树中元素的重排
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                        // e是链表的头并且e.next!=null，那么处理链表中元素重排
                    else { // preserve order
                        // loHead,loTail 代表扩容后不用变换下标，见注1
                        Node<K,V> loHead = null, loTail = null;
                        // hiHead,hiTail 代表扩容后变换下标，见注1
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        // 遍历链表
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    // 初始化head指向链表当前元素e，e不一定是链表的第一个元素，初始化后loHead
                                    // 代表下标保持不变的链表的头元素
                                    loHead = e;
                                else
                                    // loTail.next指向当前e
                                    loTail.next = e;
                                // loTail指向当前的元素e
                                // 初始化后，loTail和loHead指向相同的内存，所以当loTail.next指向下一个元素时，
                                // 底层数组中的元素的next引用也相应发生变化，造成lowHead.next.next.....
                                // 跟随loTail同步，使得lowHead可以链接到所有属于该链表的元素。
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    // 初始化head指向链表当前元素e, 初始化后hiHead代表下标更改的链表头元素
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        // 遍历结束, 将tail指向null，并把链表头放入新数组的相应下标，形成新的映射。
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

















}
