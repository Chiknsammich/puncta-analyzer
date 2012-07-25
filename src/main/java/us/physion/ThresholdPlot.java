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

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
 
/**
 * Plots threshold
 * 
 * @author Barry Wark
 */
public class ThresholdPlot extends Canvas implements Measurements, MouseListener {
    
   static final int WIDTH = 256;
   static final int HEIGHT = 64;
   double minThreshold = 85.0D;
   double maxThreshold = 255.0D;
   int[] histogram;
   Color[] hColors;
   int hmax;
   Image os;
   Graphics osg;
   boolean blackAndWhite;
 
   public ThresholdPlot() {
      addMouseListener(this);
      setSize(257, 65);
   }
 
   void setHistogram(ImagePlus imp) {
      ImageProcessor ip = imp.getProcessor();
      if (!(ip instanceof ByteProcessor)) {
         double min = ip.getMin();
         double max = ip.getMax();
         ip.setMinAndMax(min, max);
         ip = new ByteProcessor(ip.createImage());
      }
      
      ip.setMask(imp.getMask());
      ImageStatistics stats = ImageStatistics.getStatistics(ip, 9, null);
      int maxCount2 = 0;
      this.histogram = stats.histogram;
      for (int i = 0; i < stats.nBins; i++) {
         if ((this.histogram[i] > maxCount2) && (i != stats.mode)) {
            maxCount2 = this.histogram[i];
         }
      }
      
      this.hmax = stats.maxCount;
      if ((this.hmax > maxCount2 * 2) && (maxCount2 != 0)) {
          this.hmax = (int)(maxCount2 * 1.5D);
          this.histogram[stats.mode] = this.hmax;
      }
     
      this.os = null;
 
      ColorModel cm = ip.getColorModel();
      if (!(cm instanceof IndexColorModel)) {
         return;
      }
      IndexColorModel icm = (IndexColorModel)cm;
      int mapSize = icm.getMapSize();
      if (mapSize != 256) {
        return;
      }
      byte[] r = new byte[256];
      byte[] g = new byte[256];
      byte[] b = new byte[256];
      icm.getReds(r);
      icm.getGreens(g);
      icm.getBlues(b);
      this.hColors = new Color[256];
      for (int i = 0; i < 256; i++) {
         this.hColors[i] = new Color(r[i] & 0xFF, g[i] & 0xFF, b[i] & 0xFF);
      }
   }
 
   public void update(Graphics g) {
      paint(g);
   }
 
   public void paint(Graphics g) {
      if (this.histogram != null) {
         if (this.os == null) {
            this.os = createImage(256, 64);
            this.osg = this.os.getGraphics();
            this.osg.setColor(Color.white);
            this.osg.fillRect(0, 0, 256, 64);
            this.osg.setColor(Color.gray);
            for (int i = 0; i < 256; i++) {
               if (this.hColors != null) this.osg.setColor(this.hColors[i]);
                  this.osg.drawLine(i, 64, i, 64 - 64 * this.histogram[i] / this.hmax);
            }
            this.osg.dispose();
         }
         g.drawImage(this.os, 0, 0, this);
     } 
     else {
        g.setColor(Color.white);
        g.fillRect(0, 0, 256, 64);
     }
     g.setColor(Color.black);
     g.drawRect(0, 0, 256, 64);
     if (!this.blackAndWhite)
        g.setColor(Color.red);
     g.drawRect((int)this.minThreshold, 1, (int)(this.maxThreshold - this.minThreshold), 64);
   }
 
   public void mousePressed(MouseEvent e)
   {
   }
 
   public void mouseReleased(MouseEvent e)
   {
   }
 
   public void mouseExited(MouseEvent e)
   {
   }
 
   public void mouseClicked(MouseEvent e)
   {
   }
 
   public void mouseEntered(MouseEvent e)
   {
   }
}

