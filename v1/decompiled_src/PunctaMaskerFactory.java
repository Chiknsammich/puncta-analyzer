/*    */ public class PunctaMaskerFactory
/*    */ {
/*    */   public static MaskerIF getMasker()
/*    */   {
/* 23 */     return new ThresholdMasker();
/*    */   }
/*    */ }

/* Location:           /Users/todd/Projects/puncta-analyzer/v1/PunctaAnalyzer/
 * Qualified Name:     PunctaMaskerFactory
 * JD-Core Version:    0.6.0
 */