package net.hatran;

public class DirectedEdge {
    String StartPtIndex; // 起点
    String EndPtIndex; // 终点
    double IncludedAngle; // 夹角
    double Length; // 长度
    int UsageCount; // 扩展边的使用次数

    public DirectedEdge()
    {

    }

    public void setStartPtAndEndPt(String startIndex, String endIndex)
    {
        this.StartPtIndex = startIndex;
        this.EndPtIndex = endIndex;
    }

    public DirectedEdge(String startIndex, String endIndex)
    {
        this.StartPtIndex = startIndex;
        this.EndPtIndex = endIndex;
        this.UsageCount = 0;
    }

    public boolean equal(DirectedEdge e)
    {
//		if(this.StartPt.equal(e.StartPt) && this.EndPt.equal(e.EndPt))
//		{
//			System.out.println("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
//			this.displayDirectedEdge();
//			e.displayDirectedEdge();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}

        return ((this.StartPtIndex.equals(e.StartPtIndex)&&this.EndPtIndex.equals(e.EndPtIndex)) || (this.StartPtIndex.equals(e.EndPtIndex)&&this.EndPtIndex.equals(e.StartPtIndex)));
//		return (this.StartPt.equal(e.EndPt) && this.EndPt.equal(e.StartPt));
    }

    public void displayDirectedEdge()
    {
        System.out.println("(" + this.StartPtIndex + ")——>(" + this.EndPtIndex + ") " + this.UsageCount + "  " + this.Length);
//		System.out.println(this.Length);
    }
}

