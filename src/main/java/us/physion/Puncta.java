/*
 * #%L
 * Puncta Analyzer is an ImageJ plugin for detecting and quantifying punctate 
 * colocalization in multi-channel images.
 * %%
 * Copyright (C) 2012, Physion Consulting LLC All rights reserved.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package us.physion;

import java.awt.geom.Point2D;

/**
 * Defines a Puncta
 * 
 * @author Barry Wark
 */
public class Puncta {
   protected Point2D.Double loc;
   protected double area;
   protected double perimeter;
   protected double max;
   protected double min;
   protected double mean;
 
   public Puncta(double x, double y) {
      this.loc = new Point2D.Double(x, y);
   }
 
   public Puncta(double x, 
                 double y, 
                 double area, 
                 double perimeter, 
                 double max, 
                 double min, 
                 double mean) {
      this(x, y);
      this.area = area;
      this.perimeter = perimeter;
      this.max = max;
      this.min = min;
      this.mean = mean;
   }
 
   public Puncta(double x, 
                 double y, 
                 double area, 
                 double max, 
                 double min, 
                 double mean) {
      this(x, y);
      this.area = area;
      this.max = max;
      this.min = min;
      this.mean = mean;
   }
 
   public double perimeter() {
      return this.perimeter;
   }
 
   public double max() {
      return this.max;
   }
 
   public double min() {
      return this.min;
   }
 
   public double mean() {
      return this.mean;
   }
 
   public double area() {
      return this.area;
   }
 
   public double distanceTo(Puncta p) {
      return this.loc.distance(p.loc());
   }
 
   public double getX() {
      return this.loc.getX();
   }
 
   public double getY() {
      return this.loc.getY();
   }
 
   public Point2D.Double loc() {
      return (Point2D.Double)this.loc.clone();
   }
}

