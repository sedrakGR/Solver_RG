package org.ppit.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Pair is a Class for holding a pair of objects.
 * @author Vahan Tadevosyan
 */

public class Pair<T1, T2> implements Comparable<Pair<T1, T2>>, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4186218377613233878L;

	/**
     * Direct access is deprecated.  Use first().
     *
     * @serial
     */
    public T1 first;

    /**
     * Direct access is deprecated.  Use second().
     *
     * @serial
     */
    public T2 second;

    public Pair() {
        // first = null; second = null; -- default initialization
    }

    public Pair(T1 first, T2 second) {
        this .first = first;
        this .second = second;
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return second;
    }

    public void setFirst(T1 o) {
        first = o;
    }

    public void setSecond(T2 o) {
        second = o;
    }

    public String toString() {
        return "(" + first + "," + second + ")";
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o instanceof  Pair) {
            Pair p = (Pair) o;
            return (first == null ? p.first == null : first
                    .equals(p.first))
                    && (second == null ? p.second == null : second
                            .equals(p.second));
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (((first == null) ? 0 : first.hashCode()) << 16) ^ ((second == null) ? 0 : second.hashCode());
    }

    /**
     * Read a string representation of a Pair from a DataStream.
     * This might not work correctly unless the pair of objects are of type
     * <code>String</code>.
     */
    public static Pair<String, String> readStringPair(DataInputStream in) {
        Pair<String, String> p = new Pair<String, String>();
        try {
            p.first = in.readUTF();
            p.second = in.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * Write a string representation of a Pair from a DataStream.
     * The <code>toString()</code> method is called on each of the pair
     * of objects and a <code>String</code> representation is written.
     * This might not allow one to recover the pair of objects unless they
     * are of type <code>String</code>.
     */
    public void save(DataOutputStream out) {
        try {
            out.writeUTF(first.toString());
            out.writeUTF(second.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Compares this <code>Pair</code> to another object.
     * If the object is a <code>Pair</code>, this function will work providing
     * the elements of the <code>Pair</code> are themselves comparable.
     * It will then return a value based on the pair of objects, where
     * <code>p &gt; q iff p.first() &gt; q.first() ||
     * (p.first().equals(q.first()) && p.second() &gt; q.second())</code>.
     * If the other object is not a <code>Pair</code>, it throws a
     * <code>ClassCastException</code>.
     *
     * @param another the <code>Object</code> to be compared.
     * @return the value <code>0</code> if the argument is a
     *         <code>Pair</code> equal to this <code>Pair</code>; a value less than
     *         <code>0</code> if the argument is a <code>Pair</code>
     *         greater than this <code>Pair</code>; and a value
     *         greater than <code>0</code> if the argument is a
     *         <code>Pair</code> less than this <code>Pair</code>.
     * @throws ClassCastException if the argument is not a
     *                            <code>Pair</code>.
     * @see java.lang.Comparable
     */
    public int compareTo(Pair<T1, T2> another) {
        int comp = ((Comparable) first()).compareTo(another.first());
        if (comp != 0) {
            return comp;
        } else {
            return ((Comparable) second()).compareTo(another.second());
        }
    }
}
