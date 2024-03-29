package com.yandex_backend_school.mega_market.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * @author zerdicorp
 * @project mega_market
 * @created 22/06/2022 - 11:37 AM
 */

public class SpiedArrayList<T> implements List<T> {
  private final List<T> list = new ArrayList<>();

  public void sort(Comparator<? super T> comparator) {
    list.sort(comparator);
  }

  public void forEach(Consumer<? super T> action) {
    list.forEach(action);
  }

  public boolean add(T t) {
    return list.add(t);
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  @Override
  public Iterator<T> iterator() {
    return null;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public <T1> T1[] toArray(T1[] t1s) {
    return null;
  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends T> collection) {
    return false;
  }

  @Override
  public boolean addAll(int i, Collection<? extends T> collection) {
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return false;
  }

  @Override
  public void clear() {

  }

  @Override
  public boolean equals(Object o) {
    return false;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public T get(int i) {
    return null;
  }

  @Override
  public T set(int i, T t) {
    return null;
  }

  @Override
  public void add(int i, T t) {

  }

  @Override
  public T remove(int i) {
    return null;
  }

  @Override
  public int indexOf(Object o) {
    return 0;
  }

  @Override
  public int lastIndexOf(Object o) {
    return 0;
  }

  @Override
  public ListIterator<T> listIterator() {
    return null;
  }

  @Override
  public ListIterator<T> listIterator(int i) {
    return null;
  }

  @Override
  public List<T> subList(int i, int i1) {
    return null;
  }
}
