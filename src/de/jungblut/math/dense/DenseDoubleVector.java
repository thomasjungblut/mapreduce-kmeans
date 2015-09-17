package de.jungblut.math.dense;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.google.common.collect.AbstractIterator;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.function.DoubleDoubleVectorFunction;
import de.jungblut.math.function.DoubleVectorFunction;

/**
 * Dense double vector implementation.
 */
public final class DenseDoubleVector implements DoubleVector {

  private final double[] vector;

  /**
   * Creates a new vector with the given length.
   */
  public DenseDoubleVector(int length) {
    this.vector = new double[length];
  }

  /**
   * Creates a new vector with the given length and default value.
   * 
   * @param length the length of the new vector.
   * @param the value of all the vector elements.
   */
  public DenseDoubleVector(int length, double val) {
    this(length);
    Arrays.fill(vector, val);
  }

  /**
   * Creates a new vector with the given array. This wraps a copy of the given
   * array.
   */
  public DenseDoubleVector(double[] arr) {
    this.vector = new double[arr.length];
    System.arraycopy(arr, 0, this.vector, 0, arr.length);
  }

  /**
   * Creates a new vector with the given array and the last value 'lastValue'.
   * This resulting vector will be of size array.length+1.
   * 
   * @param array the first part of the vector.
   * @param lastValue the element that will be at index length-1 (last position)
   *          in the resulting vector.
   */
  public DenseDoubleVector(double[] array, double lastValue) {
    this.vector = new double[array.length + 1];
    System.arraycopy(array, 0, this.vector, 0, array.length);
    this.vector[array.length] = lastValue;
  }

  /**
   * Creates a new vector with the given array and the first value firstElement.
   * 
   * @param firstElement the element that will be at index 0 (first position) in
   *          the resulting vector.
   * @param array the rest of the array for the vector.
   */
  public DenseDoubleVector(double firstElement, double[] array) {
    this.vector = new double[array.length + 1];
    this.vector[0] = firstElement;
    System.arraycopy(array, 0, this.vector, 1, array.length);
  }

  /**
   * Transforms the given vector into this vector.
   * 
   * @param vec a double vector.
   */
  public DenseDoubleVector(DoubleVector vec) {
    this.vector = new double[vec.getDimension()];

    if (vec.isSparse()) {
      Iterator<DoubleVectorElement> iterateNonZero = vec.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        this.vector[next.getIndex()] = next.getValue();
      }
    } else {
      System.arraycopy(vec.toArray(), 0, this.vector, 0, this.vector.length);
    }

  }

  @Override
  public final double get(int index) {
    return vector[index];
  }

  @Override
  public final int getLength() {
    return vector.length;
  }

  @Override
  public int getDimension() {
    return getLength();
  }

  @Override
  public final void set(int index, double value) {
    vector[index] = value;
  }

  @Override
  public DoubleVector apply(DoubleVectorFunction func) {
    DenseDoubleVector newV = new DenseDoubleVector(this.vector);
    for (int i = 0; i < vector.length; i++) {
      newV.vector[i] = func.calculate(i, vector[i]);
    }
    return newV;
  }

  @Override
  public DoubleVector apply(DoubleVector other, DoubleDoubleVectorFunction func) {
    DenseDoubleVector newV = (DenseDoubleVector) deepCopy();
    for (int i = 0; i < vector.length; i++) {
      newV.vector[i] = func.calculate(i, vector[i], other.get(i));
    }
    return newV;
  }

  @Override
  public final DoubleVector add(DoubleVector v) {
    DoubleVector newv = null;
    if (v.isSparse()) {
      newv = new DenseDoubleVector(vector);
      Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        newv.set(next.getIndex(), this.get(next.getIndex()) + next.getValue());
      }
    } else {
      newv = new DenseDoubleVector(this.getLength());
      for (int i = 0; i < v.getLength(); i++) {
        newv.set(i, this.get(i) + v.get(i));
      }
    }
    return newv;
  }

  @Override
  public final DoubleVector add(double scalar) {
    DoubleVector newv = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < this.getLength(); i++) {
      newv.set(i, this.get(i) + scalar);
    }
    return newv;
  }

  @Override
  public final DoubleVector subtract(DoubleVector v) {
    DoubleVector newv = null;
    if (v.isSparse()) {
      newv = new DenseDoubleVector(vector);
      Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        newv.set(next.getIndex(), this.get(next.getIndex()) - next.getValue());
      }
    } else {
      newv = new DenseDoubleVector(this.getLength());
      for (int i = 0; i < v.getLength(); i++) {
        newv.set(i, this.get(i) - v.get(i));
      }
    }
    return newv;
  }

  @Override
  public final DoubleVector subtract(double v) {
    DenseDoubleVector newv = new DenseDoubleVector(vector.length);
    for (int i = 0; i < vector.length; i++) {
      newv.set(i, vector[i] - v);
    }
    return newv;
  }

  @Override
  public final DoubleVector subtractFrom(double v) {
    DenseDoubleVector newv = new DenseDoubleVector(vector.length);
    for (int i = 0; i < vector.length; i++) {
      newv.set(i, v - vector[i]);
    }
    return newv;
  }

  @Override
  public DoubleVector multiply(double scalar) {
    DoubleVector v = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < v.getLength(); i++) {
      v.set(i, this.get(i) * scalar);
    }
    return v;
  }

  @Override
  public DoubleVector multiply(DoubleVector v) {
    DoubleVector newv = new DenseDoubleVector(this.getLength());
    if (v.isSparse()) {
      Iterator<DoubleVectorElement> iterateNonZero = v.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        newv.set(next.getIndex(), this.get(next.getIndex()) * next.getValue());
      }
    } else {
      for (int i = 0; i < v.getLength(); i++) {
        newv.set(i, this.get(i) * v.get(i));
      }
    }
    return newv;
  }

  @Override
  public DoubleVector divide(double scalar) {
    if (scalar == 0d) {
      throw new java.lang.ArithmeticException("/ by zero");
    }
    DenseDoubleVector v = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < v.getLength(); i++) {
      v.set(i, this.get(i) / scalar);
    }
    return v;
  }

  @Override
  public DoubleVector divideFrom(double scalar) {
    DoubleVector v = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < v.getLength(); i++) {
      if (this.get(i) != 0.0d) {
        double result = scalar / this.get(i);
        v.set(i, result);
      } else {
        throw new java.lang.ArithmeticException("/ by zero");
      }
    }
    return v;
  }

  @Override
  public DoubleVector divideFrom(DoubleVector vector) {
    DoubleVector v = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < v.getLength(); i++) {
      if (this.get(i) != 0.0d) {
        double result = vector.get(i) / this.get(i);
        v.set(i, result);
      } else {
        throw new java.lang.ArithmeticException("/ by zero");
      }
    }
    return v;
  }

  @Override
  public DoubleVector divide(DoubleVector vector) {
    DoubleVector v = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < v.getLength(); i++) {
      if (vector.get(i) != 0.0d) {
        double result = this.get(i) / vector.get(i);
        v.set(i, result);
      } else {
        throw new java.lang.ArithmeticException("/ by zero");
      }
    }
    return v;
  }

  @Override
  public DoubleVector pow(double x) {
    DenseDoubleVector v = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < v.getLength(); i++) {
      double value = 0.0d;
      // it is faster to multiply when we having ^2
      if (x == 2d) {
        value = vector[i] * vector[i];
      } else {
        value = FastMath.pow(vector[i], x);
      }
      v.set(i, value);
    }
    return v;
  }

  @Override
  public DoubleVector sqrt() {
    DoubleVector v = new DenseDoubleVector(this.getLength());
    for (int i = 0; i < v.getLength(); i++) {
      v.set(i, FastMath.sqrt(vector[i]));
    }
    return v;
  }

  @Override
  public double sum() {
    double sum = 0.0d;
    for (double aVector : vector) {
      sum += aVector;
    }
    return sum;
  }

  @Override
  public DoubleVector abs() {
    DoubleVector v = new DenseDoubleVector(getLength());
    for (int i = 0; i < v.getLength(); i++) {
      v.set(i, FastMath.abs(vector[i]));
    }
    return v;
  }

  @Override
  public double dot(DoubleVector s) {
    double dotProduct = 0.0d;
    if (s.isSparse()) {
      Iterator<DoubleVectorElement> iterateNonZero = s.iterateNonZero();
      while (iterateNonZero.hasNext()) {
        DoubleVectorElement next = iterateNonZero.next();
        dotProduct += this.get(next.getIndex()) * next.getValue();
      }
    } else {
      for (int i = 0; i < getLength(); i++) {
        dotProduct += this.get(i) * s.get(i);
      }
    }
    return dotProduct;
  }

  @Override
  public DoubleVector slice(int length) {
    return slice(0, length);
  }

  @Override
  public DoubleVector slice(int start, int end) {
    DoubleVector nv = new DenseDoubleVector(end - start);
    int index = 0;
    for (int i = start; i < end; i++) {
      nv.set(index++, vector[i]);
    }
    return nv;
  }

  @Override
  public DoubleVector sliceByLength(int start, int length) {
    DoubleVector nv = new DenseDoubleVector(length);
    int index = start;
    for (int i = 0; i < length; i++) {
      nv.set(i, vector[index++]);
    }
    return nv;
  }

  @Override
  public double max() {
    double max = -Double.MAX_VALUE;
    for (int i = 0; i < getLength(); i++) {
      double d = vector[i];
      if (d > max) {
        max = d;
      }
    }
    return max;
  }

  @Override
  public int maxIndex() {
    double max = -Double.MAX_VALUE;
    int maxIndex = 0;
    for (int i = 0; i < getLength(); i++) {
      double d = vector[i];
      if (d > max) {
        max = d;
        maxIndex = i;
      }
    }
    return maxIndex;
  }

  @Override
  public double min() {
    double min = Double.MAX_VALUE;
    for (int i = 0; i < getLength(); i++) {
      double d = vector[i];
      if (d < min) {
        min = d;
      }
    }
    return min;
  }

  @Override
  public int minIndex() {
    double min = Double.MAX_VALUE;
    int minIndex = 0;
    for (int i = 0; i < getLength(); i++) {
      double d = vector[i];
      if (d < min) {
        min = d;
        minIndex = i;
      }
    }
    return minIndex;
  }

  @Override
  public final double[] toArray() {
    return vector;
  }

  @Override
  public boolean isSparse() {
    return false;
  }

  @Override
  public boolean isSingle() {
    return false;
  }

  @Override
  public DoubleVector deepCopy() {
    final double[] src = vector;
    final double[] dest = new double[vector.length];
    System.arraycopy(src, 0, dest, 0, vector.length);
    return new DenseDoubleVector(dest);
  }

  @Override
  public Iterator<DoubleVectorElement> iterateNonZero() {
    return new NonZeroIterator();
  }

  @Override
  public Iterator<DoubleVectorElement> iterate() {
    return new DefaultIterator();
  }

  @Override
  public DoubleVector log() {
    DoubleVector v = new DenseDoubleVector(getLength());
    for (int i = 0; i < v.getLength(); i++) {
      v.set(i, FastMath.log(vector[i]));
    }
    return v;
  }

  @Override
  public DoubleVector exp() {
    DoubleVector v = new DenseDoubleVector(getLength());
    for (int i = 0; i < v.getLength(); i++) {
      v.set(i, FastMath.exp(vector[i]));
    }
    return v;
  }

  @Override
  public final String toString() {
    if (getLength() < 50) {
      return Arrays.toString(vector);
    } else {
      return getLength() + "x1";
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(vector);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DenseDoubleVector other = (DenseDoubleVector) obj;
    return Arrays.equals(vector, other.vector);
  }

  /**
   * Non-zero iterator for vector elements.
   */
  private final class NonZeroIterator extends
      AbstractIterator<DoubleVectorElement> {

    private final DoubleVectorElement element = new DoubleVectorElement();
    private final double[] array;
    private int currentIndex = 0;

    private NonZeroIterator() {
      this.array = vector;
    }

    @Override
    protected final DoubleVectorElement computeNext() {
      if (currentIndex >= array.length)
        return endOfData();
      while (array[currentIndex] == 0.0d) {
        currentIndex++;
        if (currentIndex >= array.length)
          return endOfData();
      }
      element.setIndex(currentIndex);
      element.setValue(array[currentIndex]);
      currentIndex++;
      return element;
    }
  }

  /**
   * Iterator for all elements.
   */
  private final class DefaultIterator extends
      AbstractIterator<DoubleVectorElement> {

    private final DoubleVectorElement element = new DoubleVectorElement();
    private final double[] array;
    private int currentIndex = 0;

    private DefaultIterator() {
      this.array = vector;
    }

    @Override
    protected final DoubleVectorElement computeNext() {
      if (currentIndex < array.length) {
        element.setIndex(currentIndex);
        element.setValue(array[currentIndex]);
        currentIndex++;
        return element;
      } else {
        return endOfData();
      }
    }

  }

  /**
   * @return a new vector with dimension num and a default value of 1.
   */
  public static DenseDoubleVector ones(int num) {
    return new DenseDoubleVector(num, 1.0d);
  }

  /**
   * @return a new vector with dimension num and a default value of 0.
   */
  public static DenseDoubleVector zeros(int num) {
    return new DenseDoubleVector(num);
  }

  /**
   * @return a new vector filled from index, to index, with a given stepsize.
   */
  public static DenseDoubleVector fromUpTo(double from, double to,
      double stepsize) {
    DenseDoubleVector v = new DenseDoubleVector(
        (int) (FastMath.round(((to - from) / stepsize) + 0.5)));

    for (int i = 0; i < v.getLength(); i++) {
      v.set(i, from + i * stepsize);
    }
    return v;
  }

  @Override
  public boolean isNamed() {
    return false;
  }

  @Override
  public String getName() {
    return null;
  }

}
