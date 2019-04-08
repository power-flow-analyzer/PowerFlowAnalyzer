package net.ee.pfanalyzer.io;

import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import net.ee.pfanalyzer.PowerFlowAnalyzer;

public class MatlabInterface {
	
	private static String CLASS_MATLAB = "com.mathworks.jmi.Matlab";
	private static String CLASS_MATLAB_EXCEPTION = "com.mathworks.jmi.MatlabException";
	private static String METHOD_WHEN_MATLAB_IDLE = "whenMatlabIdle";
	private static String METHOD_MT_FEVAL = "mtFeval";
	private static String METHOD_MT_FEVAL_CONSOLE_OUTPUT = "mtFevalConsoleOutput";

	public static void callMatlabFunction(final String functionName, final Object[] parameters, final int returnValueCount, 
			final PowerFlowAnalyzer applicationFrame, final boolean cancelScriptExecution, final boolean printOutput) {
		try {
			executeMatlabAsync(new Runnable() {
				@Override
				public void run() {
					if(cancelScriptExecution)
						return;
					try {
						callMatlabFunction(functionName, parameters, returnValueCount, printOutput);
					} catch (MatlabNativeException e) {
						// this is most probably a problem from Matlab side
						JOptionPane.showMessageDialog(applicationFrame, "Error (1): " + e.getMessage()
								+ "\n\nSee the Matlab console for more information.");
						applicationFrame.cancelPowerFlow();
					} catch (Exception e) {
						if(e.getCause() != null && e.getCause().getClass().getName().equals(CLASS_MATLAB_EXCEPTION)) {
							// this is most probably a problem from Matlab side
							JOptionPane.showMessageDialog(applicationFrame, "Error (2): " + e.getCause().getMessage()
									+ "\n\nSee the Matlab console for more information.");
						} else {
							// this is most probably a problem from Java side
							JOptionPane.showMessageDialog(applicationFrame, "An error occurred while executing a matlab function: " + e
									+ "\n\nSee the Matlab console for more information.");
						}
						applicationFrame.cancelPowerFlow();
					}
				}
			});
		} catch(Exception exception) {
			// this is most probably a problem from Java side
			JOptionPane.showMessageDialog(applicationFrame, "An error occurred while calling matlab: " + exception
					+ "\n\nSee the Matlab console for more information.");
			exception.printStackTrace();
		}
	}

	private static void executeMatlabAsync(Runnable matlabFunctionCall) throws Exception {
		Class<?> matlabClass = Class.forName(CLASS_MATLAB);
		Method runMethod =  matlabClass.getMethod(METHOD_WHEN_MATLAB_IDLE, Runnable.class);
		runMethod.invoke(null, matlabFunctionCall);
	}
	
	private static void callMatlabFunction(String functionName, Object[] parameters, 
			int returnValueCount, boolean printOutput) throws MatlabNativeException, Exception {
		try {
			Class<?> matlabClass = Class.forName(CLASS_MATLAB);
			String methodName = printOutput ? METHOD_MT_FEVAL_CONSOLE_OUTPUT : METHOD_MT_FEVAL;
			Method runMethod =  matlabClass.getMethod(methodName, String.class, Object[].class, int.class);
			runMethod.invoke(null, functionName, parameters, returnValueCount);
		} catch (Exception exception) {
			if(exception.getClass().getName().equals(CLASS_MATLAB_EXCEPTION))
				throw new MatlabNativeException(exception);
			else
				throw exception;
		}
	}
}