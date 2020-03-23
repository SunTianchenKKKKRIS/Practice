package SourceCode;

public class LinkedList {

/**
 * LinkedList 的继承体系较为复杂，继承自 AbstractSequentialList，同时又实现了 List 和 Deque 接口
 *
 * LinkedList 继承自 AbstractSequentialList，AbstractSequentialList 又是什么呢？从实现上，AbstractSequentialList 提供了一套基于顺序访问的接口。通过继承此类，
 * 子类仅需实现部分代码即可拥有完整的一套访问某种序列表（比如链表）的接口。深入源码，AbstractSequentialList 提供的方法基本上都是通过 ListIterator 实现的，比如：
 */

//AbstractSequentialList是顺序访问集合的抽象类
//随机访问集合类一般建议继承 AbstractList 而不是 AbstractSequentialList

public E get(int index) {
    try {
        return listIterator(index).next();
    } catch (NoSuchElementException exc) {
        throw new IndexOutOfBoundsException("Index: "+index);
    }
}

    public void add(int index, E element) {
        try {
            listIterator(index).add(element);
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }

    // 留给子类实现
    public abstract ListIterator<E> listIterator(int index);

/**
 * 队列和栈都可以利用LinkedList实现 例如
 * Queue q = new LinkedList()
 * 一般来说栈都建议自己利用LinkedList重写一个  原Stack类实现的不好
 */


/**
 * inkedList 底层基于链表结构，无法向 ArrayList 那样随机访问指定位置的元素。
 * LinkedList 查找过程要稍麻烦一些，需要从链表头结点（或尾节点）向后查找，时间复杂度为 O(N)。相关源码如下：
 */
public E get(int index) {
    checkElementIndex(index);
    return node(index).item;
}

    Node<E> node(int index) {
        /*
         * 查找位置 index 如果小于节点数量的一半，  为了缩短时间消耗
         * 则从头节点开始查找，否则从尾节点查找
         */
        if (index < (size >> 1)) {
            Node<E> x = first;
            // 循环向后查找，直至 i == index
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    /**
     * 这个方法代码比较简单
     * 唯一需要看的就是  在查询时先将index和size/2比较  能够缩短一些时间消耗
     */

    /**
     * 遍历
     * 对于 LinkedList 的遍历还是需要注意一些，不然可能会导致代码效率低下。通常情况下，我们会使用 foreach 遍历 LinkedList，
     * 而 foreach 最终转换成迭代器形式。所以分析 LinkedList 的遍历的核心就是它的迭代器实现，相关代码如下：
     */

    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        /** 构造方法将 next 引用指向指定位置的节点 */
        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index);   //这里用了上面的node()
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;    // 调用 next 方法后，next 引用都会指向他的后继节点
            nextIndex++;
            return lastReturned.item;
        }

        // 省略部分方法
    }

    //这里要说一下LinkedList的遍历效率问题
    List<Integet> list = new LinkedList<>();
            list.add(1)
            list.add(2)
            ......
            for (int i = 0; i < list.size(); i++) {
            Integet item = list.get(i);
            // do something
    }

    /**
     * 例如上面代码 多次对LInkedList进行遍历查询  每次都要从链表头或尾遍历  导致效率极低  建议在开发中不要使用这种用法
     */

    /**
     * 插入
     * 实际就是数据结构中链表插入新元素的算法  过程就是断链  挂链
     */

    /** 在链表尾部插入元素 */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /** 在链表指定位置插入元素 */
    public void add(int index, E element) {
        checkPositionIndex(index);

        // 判断 index 是不是链表尾部位置，如果是，直接将元素节点插入链表尾部即可
        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    /** 将元素节点插入到链表尾部 */
    void linkLast(E e) {
        final Node<E> l = last;
        // 创建节点，并指定节点前驱为链表尾节点 last，后继引用为空
        final Node<E> newNode = new Node<>(l, e, null);
        // 将 last 引用指向新节点
        last = newNode;//把新节点放到last
        // 判断尾节点是否为空，为空表示当前链表还没有节点
        if (l == null)
            first = newNode;
        else
            l.next = newNode;    // 让原尾节点后继引用 next 指向新的尾节点
        size++;
        modCount++;
    }

    /** 将元素节点插入到 succ 之前的位置 */
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        // 1. 初始化节点，并指明前驱和后继节点
        final Node<E> newNode = new Node<>(pred, e, succ);
        // 2. 将 succ 节点前驱引用 prev 指向新节点
        succ.prev = newNode;
        // 判断尾节点是否为空，为空表示当前链表还没有节点
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;   // 3. succ 节点前驱的后继引用指向新节点
        size++;
        modCount++;
    }

    /**
     * 删除
     */

    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            // 遍历链表，找到要删除的节点
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);    // 将节点从链表中移除
                    return true;
                }
            }
        }
        return false;
    }

    public E remove(int index) {
        checkElementIndex(index);
        // 通过 node 方法定位节点，并调用 unlink 将节点从链表中移除
        return unlink(node(index));
    }

    /** 将某个节点从链表中移除 */
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        // prev 为空，表明删除的是头节点
        if (prev == null) {
            first = next;
        } else {
            // 将 x 的前驱的后继指向 x 的后继
            prev.next = next;
            // 将 x 的前驱引用置空，断开与前驱的链接
            x.prev = null;
        }

        // next 为空，表明删除的是尾节点
        if (next == null) {
            last = prev;
        } else {
            // 将 x 的后继的前驱指向 x 的前驱
            next.prev = prev;
            // 将 x 的后继引用置空，断开与后继的链接
            x.next = null;
        }

        // 将 item 置空，方便 GC 回收
        x.item = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 总的来说 LinkedList比较简单 和数据结构中的链表操作基本一致  比较容易理解
     */





}
