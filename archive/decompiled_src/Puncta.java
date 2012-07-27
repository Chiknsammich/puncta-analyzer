/*     */ import java.awt.geom.Point2D.Double;
/*     */ 
/*     */ public class Puncta
/*     */ {
/*     */   protected Point2D.Double loc;
/*     */   protected double area;
/*     */   protected double perimeter;
/*     */   protected double max;
/*     */   protected double min;
/*     */   protected double mean;
/*     */ 
/*     */   public Puncta(double x, double y)
/*     */   {
/*  27 */     this.loc = new Point2D.Double(x, y);
/*     */   }
/*     */ 
/*     */   public Puncta(double x, double y, double area, double perimeter, double max, double min, double mean) {
/*  31 */     this(x, y);
/*  32 */     this.area = area;
/*  33 */     this.perimeter = perimeter;
/*  34 */     this.max = max;
/*  35 */     this.min = min;
/*  36 */     this.mean = mean;
/*     */   }
/*     */ 
/*     */   public Puncta(double x, double y, double area, double max, double min, double mean) {
/*  40 */     this(x, y);
/*  41 */     this.area = area;
/*  42 */     this.max = max;
/*  43 */     this.min = min;
/*  44 */     this.mean = mean;
/*     */   }
/*     */ 
/*     */   public double perimeter()
/*     */   {
/*  50 */     return this.perimeter;
/*     */   }
/*     */ 
/*     */   public double max() {
/*  54 */     return this.max;
/*     */   }
/*     */ 
/*     */   public double min() {
/*  58 */     return this.min;
/*     */   }
/*     */ 
/*     */   public double mean() {
/*  62 */     return this.mean;
/*     */   }
/*     */ 
/*     */   public double area()
/*     */   {
/*  69 */     return this.area;
/*     */   }
/*     */ 
/*     */   public double distanceTo(Puncta p)
/*     */   {
/*  78 */     return this.loc.distance(p.loc());
/*     */   }
/*     */ 
/*     */   public double getX()
/*     */   {
/*  86 */     return this.loc.getX();
/*     */   }
/*     */ 
/*     */   public double getY()
/*     */   {
/*  94 */     return this.loc.getY();
/*     */   }
/*     */ 
/*     */   public Point2D.Double loc()
/*     */   {
/* 101 */     return (Point2D.Double)this.loc.clone();
/*     */   }
/*     */ }

/* Location:           /Users/todd/Projects/puncta-analyzer/v1/PunctaAnalyzer/
 * Qualified Name:     Puncta
 * JD-Core Version:    0.6.0
 */