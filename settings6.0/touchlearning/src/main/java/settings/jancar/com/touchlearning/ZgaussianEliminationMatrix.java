
 /**
 * @系统名称 :wm
 * @创建人:cqf
 * @创建时间 :2013-9-7 下午2:53:14
 */
    
package settings.jancar.com.touchlearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 用消元法求n元一次方程组的解 采用了递归的方法
 * @@param q  方程组的系数矩阵 以二维数组的形式表示
 * @@return  方程组的解 以数组形式返回
 */
public class ZgaussianEliminationMatrix {
    
    public double[] caculate(double[][] q) {
        int hang = q.length; // 行数
        int shu = q[0].length; // 列数

        if (hang == 1) { // 化简到成为一元一次方程的时候
            double[] x = { q[0][1] / q[0][0] };
            return x;
        } else {
            double[][] temp = new double[hang - 1][shu - 1]; // 保存消元后的数组
            // 分开第一个系数为0 的行
            List<double[]> zerorows = new ArrayList<double[]>(); // 第一个系数为0 的行
            List<double[]> otherrows = new ArrayList<double[]>();

            for (int i = 0; i < hang; i++) {
                if (q[i][0] == 0)
                    zerorows.add(q[i]);
                else
                    otherrows.add(q[i]);
            }

            for (int i = 0; i < otherrows.size() - 1; i++) {
                for (int j = 1; j < shu; j++) {
                    temp[i][j-1] = otherrows.get(i+1)[0]*otherrows.get(i)[j]
                          - otherrows.get(i)[0]*otherrows.get(i+1)[j];
                }
            }
            for (int i = 0; i < zerorows.size(); i++) {
                for (int j = 1; j < shu; j++) {
                    temp[i + otherrows.size() - 1][j - 1] = zerorows.get(i)[j];
                }
            }
            double[] result = this.caculate(temp); // 递归,上步简化得到的结果

            // 还要先判断 第一个数的系数是否为0
            int row = 0;
            while (q[row][0] == 0) {
                row++;
            }

            double d = 0.00;
            for (int i = 1; i < shu-1; i++)
                d += q[row][i] * result[i-1]; 
            double x = (q[row][shu-1] - d) / q[row][0]; //将前面得到的结果 代入 求出当前 未知数

            double[] newresult = new double[result.length + 1];
            newresult[0] = x;
            for (int i = 0; i < result.length; i++) {
                newresult[i + 1] = result[i];
            }
            return newresult;
        }
    }
}
