/*     */ import ij.ImagePlus;
/*     */ import ij.measure.Measurements;
/*     */ import ij.process.ByteProcessor;
/*     */ import ij.process.ImageProcessor;
/*     */ import ij.process.ImageStatistics;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.awt.image.IndexColorModel;
/*     */ 
/*     */ class ThresholdPlot extends Canvas
/*     */   implements Measurements, MouseListener
/*     */ {
/*     */   static final int WIDTH = 256;
/*     */   static final int HEIGHT = 64;
/* 608 */   double minThreshold = 85.0D;
/* 609 */   double maxThreshold = 255.0D;
/*     */   int[] histogram;
/*     */   Color[] hColors;
/*     */   int hmax;
/*     */   Image os;
/*     */   Graphics osg;
/*     */   boolean blackAndWhite;
/*     */ 
/*     */   public ThresholdPlot()
/*     */   {
/* 618 */     addMouseListener(this);
/* 619 */     setSize(257, 65);
/*     */   }
/*     */ 
/*     */   void setHistogram(ImagePlus imp) {
/* 623 */     ImageProcessor ip = imp.getProcessor();
/* 624 */     if (!(ip instanceof ByteProcessor)) {
/* 625 */       double min = ip.getMin();
/* 626 */       double max = ip.getMax();
/* 627 */       ip.setMinAndMax(min, max);
/* 628 */       ip = new ByteProcessor(ip.createImage());
/*     */     }
/* 630 */     ip.setMask(imp.getMask());
/* 631 */     ImageStatistics stats = ImageStatistics.getStatistics(ip, 9, null);
/* 632 */     int maxCount2 = 0;
/* 633 */     this.histogram = stats.histogram;
/* 634 */     for (int i = 0; i < stats.nBins; i++)
/* 635 */       if ((this.histogram[i] > maxCount2) && (i != stats.mode))
/* 636 */         maxCount2 = this.histogram[i];
/* 637 */     this.hmax = stats.maxCount;
/* 638 */     if ((this.hmax > maxCount2 * 2) && (maxCount2 != 0)) {
/* 639 */       this.hmax = (int)(maxCount2 * 1.5D);
/* 640 */       this.histogram[stats.mode] = this.hmax;
/*     */     }
/* 642 */     this.os = null;
/*     */ 
/* 644 */     ColorModel cm = ip.getColorModel();
/* 645 */     if (!(cm instanceof IndexColorModel))
/* 646 */       return;
/* 647 */     IndexColorModel icm = (IndexColorModel)cm;
/* 648 */     int mapSize = icm.getMapSize();
/* 649 */     if (mapSize != 256)
/* 650 */       return;
/* 651 */     byte[] r = new byte[256];
/* 652 */     byte[] g = new byte[256];
/* 653 */     byte[] b = new byte[256];
/* 654 */     icm.getReds(r);
/* 655 */     icm.getGreens(g);
/* 656 */     icm.getBlues(b);
/* 657 */     this.hColors = new Color[256];
/* 658 */     for (int i = 0; i < 256; i++)
/* 659 */       this.hColors[i] = new Color(r[i] & 0xFF, g[i] & 0xFF, b[i] & 0xFF);
/*     */   }
/*     */ 
/*     */   public void update(Graphics g) {
/* 663 */     paint(g);
/*     */   }
/*     */ 
/*     */   public void paint(Graphics g) {
/* 667 */     if (this.histogram != null) {
/* 668 */       if (this.os == null) {
/* 669 */         this.os = createImage(256, 64);
/* 670 */         this.osg = this.os.getGraphics();
/* 671 */         this.osg.setColor(Color.white);
/* 672 */         this.osg.fillRect(0, 0, 256, 64);
/* 673 */         this.osg.setColor(Color.gray);
/* 674 */         for (int i = 0; i < 256; i++) {
/* 675 */           if (this.hColors != null) this.osg.setColor(this.hColors[i]);
/* 676 */           this.osg.drawLine(i, 64, i, 64 - 64 * this.histogram[i] / this.hmax);
/*     */         }
/* 678 */         this.osg.dispose();
/*     */       }
/* 680 */       g.drawImage(this.os, 0, 0, this);
/*     */     } else {
/* 682 */       g.setColor(Color.white);
/* 683 */       g.fillRect(0, 0, 256, 64);
/*     */     }
/* 685 */     g.setColor(Color.black);
/* 686 */     g.drawRect(0, 0, 256, 64);
/* 687 */     if (!this.blackAndWhite)
/* 688 */       g.setColor(Color.red);
/* 689 */     g.drawRect((int)this.minThreshold, 1, (int)(this.maxThreshold - this.minThreshold), 64);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e)
/*     */   {
/*     */   }
/*     */ }

/* Location:           /Users/todd/Projects/puncta-analyzer/v1/PunctaAnalyzer/
 * Qualified Name:     ThresholdPlot
 * JD-Core Version:    0.6.0
 */