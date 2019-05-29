package lib.doldory;

import lib.doldory.CrashLogger.Callback;

public class Demo {

	public static void main(String[] args) {
		
//		try {
//			int num1 = 12;
//			int num2 = 0;
//			int result = num1 / num2;
//			System.out.println(result);
//		} catch(Exception e) {
//			CrashLogger.save("D:\\tmp\\crash\\new\\id_123_log.txt", e, true);
//		}
		
		final String outVariable = "my name is: ";
		CrashLogger.asyncSend("http://localhost:8085/mofs/log_upload.do", "D:\\tmp\\crash\\new\\id_123_log.txt", new Callback() {
			
			public void call(String response) {
				
				System.out.println(outVariable + response);
			}
		});
		System.out.println("CrashLogger asyncSend fired....");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
