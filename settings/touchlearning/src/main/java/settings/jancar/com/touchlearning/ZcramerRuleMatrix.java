/**因要求解四元一次方程组, 甚至五元一次方程组, 为了避免重复的劳动遂有了写这个求解函数的想法
 * @系统名称 :wm
 * @创建人:cqf
 * @创建时间 :2013-9-7 下午2:35:38
 */
package settings.jancar.com.touchlearning;


public class ZcramerRuleMatrix {
    private double[][] savequot;// 保存变量系数
    private double[] constquot;// 保存常量系数
    private double[] saveResult;// 保存解的集合

    public ZcramerRuleMatrix(double quot[][]) {
        int count = quot.length;
        savequot = new double[count][count];
        constquot = new double[count];
        saveResult = new double[count];
        int i = 0, j = 0;
        for (i = 0; i < count; i++) {
            for (j = 0; j < count; j++) {
                savequot[i][j] = quot[i][j];
            }
            constquot[i] = quot[i][count];
            saveResult[i] = 0;
        }
    }

    private double getMatrixResult(double input[][])// 递归的方法求得某个行列式的值
    {
        if (input.length == 2)// 递归出口，为二阶行列式时，直接返回
        {
            return input[0][0] * input[1][1] - input[0][1] * input[1][0];
        } else {
            double[] temp = new double[input.length];// 存放第一列的系数值
            double[][] tempinput = new double[input.length - 1][input.length - 1];
            double result = 0;
            for (int i = 0; i < input.length; i++) {
                temp[i] = input[i][0];
                int m = 0, n = 0;
                for (int k = 0; k < input.length; k++) {
                    if (k != i) {
                        for (m = 0; m < input.length - 1; m++) {
                            tempinput[n][m] = input[k][m + 1];// 删除当前变量系数所在的行和列，得到减少一阶的新的行列式
                        }
                        n++;
                    }
                }
                if (i % 2 == 0)// 递归调用，利用代数余子式与相应系数变量的乘积之和得到多阶行列式的值
                {
                    result = result + temp[i] * getMatrixResult(tempinput);
                } else {
                    result = result - temp[i] * getMatrixResult(tempinput);
                }
            }
            return result;
        }
    }

    private double[][] getReplaceMatrix(int i)// 用常数系数替换相应的变量系数,得到新的行列式
    {
        double tempresult[][] = new double[savequot.length][savequot.length];
        for (int m = 0; m < savequot.length; m++) {
            for (int n = 0; n < savequot.length; n++) {
                if (i != m) {
                    tempresult[n][m] = savequot[n][m];
                } else {
                    tempresult[n][i] = constquot[n];// 用常量系数替换当前变量系数
                }
            }
        }
        return tempresult;
    }

    public double[] getResult() {
        double basic = 0;
        basic = getMatrixResult(savequot);// 得到变量系数行列式的值
        if (Math.abs(basic) < 0.00001) {
            System.out.println("it dose not have the queue result!");
            return saveResult;
        }
        double[][] temp = new double[saveResult.length][saveResult.length];
        for (int i = 0; i < saveResult.length; i++) {
            temp = getReplaceMatrix(i);
            saveResult[i] = getMatrixResult(temp) / basic;
        }
        return saveResult;
    }


}
