import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DealWithFile {
	public static void main(String[] args) {
//		replaceFile("//mnt//dmesg.txt");
//		long lineNumber = countLines("afterAwk.txt");
//		System.out.println(lineNumber);
        analyzeResult("afterAwk.txt");
		
	}
	
	public static void replaceFile(String fileName) {
		//处理文档，将原始结果文件中的=改成空格，为了便于筛选
		//String[] sedCmd = new String[] { "/bin/bash", "-c", "sed 's/=/ /g' " +fileName+" > //mnt//afterSed.txt"};	
		//try {
		//	Process pro = Runtime.getRuntime().exec(sedCmd);
		//	pro.waitFor();
		//} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		//通过awk打印最后一列数据，该数据是中断响应延迟数据
		String[] awkCmd = new String[] {"/bin/bash", "-c", "cat /mnt/afterSed.txt |awk '{print $NF}' > /mnt/afterAwk.txt"};
		try {
			Process pro = Runtime.getRuntime().exec(awkCmd);
			pro.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static long countLines(String fileName) {
		//统计文档里有效结果总行数
		BufferedReader br = null;
		long lineNumber = 0;
		String str = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String pattern = "^(\\d+)(.*)us";
			try {
				while((str=br.readLine()) !=null) {
					//记录以数字开头的行
					if (str.matches(pattern)) {
						++lineNumber;
						
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lineNumber;
	}
	
	public static void analyzeResult(String afterAwkFile ) {
		BufferedReader awkFile = null;
		long lineNumber = countLines(afterAwkFile);
		String lineContent = null;
		String pattern1 = "^[0-9]us";
		String pattern2 = "^1[0-9]us";
		String pattern3 = "^2[0-9]us";
		String pattern4 = "^[3-9][0-9]us";
		String pattern5 = "^\\d{3,}us";
		long lessThan10 = 0;
		long bet10And20 = 0;
		long bet20And30 = 0;
		long moreThan30 = 0;
		long atLeast100 = 0;
		
		try {
			awkFile = new BufferedReader(new FileReader(afterAwkFile));
			while((lineContent = awkFile.readLine()) != null) {
				if(lineContent.matches(pattern1)) {
					++lessThan10;
				}
				if(lineContent.matches(pattern2)) {
					++bet10And20;
				}
				if(lineContent.matches(pattern3)) {
					++bet20And30;
				}
				if(lineContent.matches(pattern4)) {
					++moreThan30;
					System.out.println("中断延迟超过30us，less than 100us具体值是:"+lineContent);
				}
				if(lineContent.matches(pattern5)){
					++atLeast100;
				        System.out.println("more than or equals 100us, details are:"+lineContent);
					}
				}
								
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				awkFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("中断触发次数："+lineNumber);
		System.out.println("中断响应延迟在10us(不包含10us)以内的次数有："+lessThan10+",占比:"+ (float)lessThan10/(float)lineNumber*100);
		System.out.println("中断响应延迟在10us至20us(不包含20us)以内的次数有："+bet10And20+",占比:"+(float)bet10And20/(float)lineNumber*100);
		System.out.println("中断响应延迟在20us至30us(不包含30us)以内的次数有："+bet20And30+",占比:"+(float)bet20And30/(float)lineNumber*100);
		System.out.println("中断响应延迟至少30us的次数有："+moreThan30+",占比:"+(float)moreThan30/(float)lineNumber*100);
		System.out.println("中断响应延迟至少100us的次数有："+atLeast100+",占比:"+(float)atLeast100/(float)lineNumber*100);
	}
}

