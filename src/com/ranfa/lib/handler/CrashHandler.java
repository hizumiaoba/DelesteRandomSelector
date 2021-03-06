package com.ranfa.lib.handler;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CompletionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ranfa.lib.CheckVersion;

public class CrashHandler {

	// Logger
		private final Logger LOG = LoggerFactory.getLogger(CrashHandler.class);
		
		// fields
		private Throwable e;
		private String description;
		private int estimateExitCode;
		private CrashReportList<String> crashReportLines = new CrashReportList<>();
		private static Random random = new Random(System.currentTimeMillis());
		
		
		// constants
		private static final String DEFAULT_DESCRIPTION = "Unexpected Error";
		private static final String[] EASTER_LINES = {
				"// It will need to do more test?",
				"// I may be so bad to code?",
				"// Shimamura Uzuki, I'll do my best!",
				"// This is also adventure!, isn't it?",
				"// I... left all books... behind... 'cause I want to talk... with you...",
				"// This software was developed by @hizumiaoba",
				"// These Easter sentences were inspired by Minecraft Crash Report!"
		};
		
		private static final int EXIT_WITH_NESTED_CAUSE = 2;
		private static final int EXIT_WITH_NULL_POINTER_EXCEPTION = 3;
		private static final int EXIT_WITH_ILLEGAL_STATE_EXCEPTION = 5;
		private static final int EXIT_WITH_COMPLETION_EXCEPTION = 7;
		private static final int EXIT_WITH_ILLEGAL_ARGUMENT_EXCEPTION = 9;
		
		public CrashHandler() {
			this(DEFAULT_DESCRIPTION);
		}
		
		public CrashHandler(String m) {
			this(m, null);
		}
		
		public CrashHandler(Throwable e) {
			this(DEFAULT_DESCRIPTION, e);
		}
		
		public CrashHandler(String description, Throwable e) {
			LOG.warn("Unexpected Exception has occured. : {}", e == null ? "null" : e.toString());
			LOG.warn("Provided Detail Message : {}", description);
			this.e = e;
			this.description = description;
			estimateExitCode = Integer.MIN_VALUE;
			crashReportLines.add("---- DelesteRandomSelector Crash Report ----");
			int randomInt = random.nextInt(EASTER_LINES.length);
			crashReportLines.add(EASTER_LINES[randomInt]);
		}
		
		public void execute() {
			if(e == null)
				throw new NullPointerException("Cannot execute crash because throwable is null.");
			estimateExitCode = calcExitCode();
			LOG.error("Cannot keep up application! : {}", e.toString());
			LOG.error("Application will exit with exit code : {}", estimateExitCode);
			LOG.error(outputReport());
			crashReportLines.outCrashReport();
			System.exit(estimateExitCode);
		}
		
		private String outputReport() {
			crashReportLines.emptyLine();
			final SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
			crashReportLines.add("Time: " + format.format(new Date()));
			crashReportLines.add("Description: " + description);
			crashReportLines.emptyLine();
			LOG.debug("Gathering exception informations.");
			if(e != null) {
				crashReportLines.add("These are simplified stack trace. (shown up to 5 lines. Full Stack trace is below.)");
				crashReportLines.add(e.toString());
				StackTraceElement[] topElements = e.getStackTrace();
				for(int i = 0; i < 5; i++) {
					crashReportLines.add("\tat " + topElements[i].toString());
				}
				crashReportLines.emptyLine();
				crashReportLines.add("A detailed walkthrough of the error, its code path and all known details is as follows:");
				crashReportLines.add("---------------------------------------------------------------------------------------");
				crashReportLines.emptyLine();
				crashReportLines.add("Stacktrace:");
				crashReportLines.add(e.getMessage());
				addLinesRecursively(e, crashReportLines);
			}
			LOG.debug("Gathering system informations.");
			RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
			OperatingSystemMXBean operatingBean = ManagementFactory.getOperatingSystemMXBean();
			MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
			MemoryUsage memUsage = memoryBean.getHeapMemoryUsage();
			crashReportLines.emptyLine();
			crashReportLines.add("-- System Details --");
			crashReportLines.add("Details:");
			crashReportLines.add("\tDelesteRandomSelector Version: " + CheckVersion.getVersion());
			crashReportLines.add("\tOperating System: "
					+ operatingBean.getName() + " ("
					+ operatingBean.getArch() + ") version "
					+ operatingBean.getVersion());
			crashReportLines.add("\tJava Version: " + runtimeBean.getSpecVersion() + ", " + runtimeBean.getVmVendor());
			crashReportLines.add("\tJava VM Version: " + runtimeBean.getVmName() + ", version " + runtimeBean.getVmVersion());
			crashReportLines.add("\tMemory: " + memUsage.getUsed() + " bytes / " + memUsage.getInit() + " bytes up to " + memUsage.getMax() + " bytes");
			crashReportLines.add("\tJVM Flags: " + runtimeBean.getInputArguments().size() + " total: " + runtimeBean.getInputArguments().toString());
			return crashReportLines.generateCrashReport();
		}
		
		public String outSystemInfo() {
			// Override fields
			this.e = null;
			this.description = "Loading screen debug info\n"
					+ "\n"
					+ "This is just a prompt for computer specs to be printed. THIS IS NOT A ERROR";
			String res = outputReport();
			LOG.info(res);
			return res;
		}
		
		public static CrashReportList<String> addLinesRecursively(Throwable e, CrashReportList<String> list) {
			if(e == null)
				return list;
			StackTraceElement[] elements = e.getStackTrace();
			for(StackTraceElement element : elements) {
				list.add("\tat " + element.toString());
			}
			if(e.getCause() != null) {
				list.add("Caused by: " + e.getCause().toString());
				addLinesRecursively(e.getCause(), list);
			}
			return list;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String desc) {
			this.description = desc;
		}
		
		public Throwable getThrowable() {
			return e;
		}
		
		public void setCause(Throwable e) {
			this.e = e;
		}
		
		public int getEstimateExitCode() {
			return estimateExitCode;
		}
		
		private int calcExitCode() {
			int res = 1;
			if(e.getCause() != null)
				res *= EXIT_WITH_NESTED_CAUSE;
			if(e instanceof NullPointerException )
				res *= EXIT_WITH_NULL_POINTER_EXCEPTION;
			if(e instanceof IllegalStateException)
				res *= EXIT_WITH_ILLEGAL_STATE_EXCEPTION;
			if(e instanceof CompletionException	)
				res *= EXIT_WITH_COMPLETION_EXCEPTION;
			if(e instanceof IllegalArgumentException)
				res *= EXIT_WITH_ILLEGAL_ARGUMENT_EXCEPTION;
			if(e.getCause() != null)
				return calcExitCode(e.getCause(), res);
			return res;
		}
		
		private static int calcExitCode(Throwable e, int current) {
			if(e == null)
				return current;
			if(e.getCause() != null)
				current *= EXIT_WITH_NESTED_CAUSE;
			if(e instanceof NullPointerException )
				current *= EXIT_WITH_NULL_POINTER_EXCEPTION;
			if(e instanceof IllegalStateException)
				current *= EXIT_WITH_ILLEGAL_STATE_EXCEPTION;
			if(e instanceof CompletionException	)
				current *= EXIT_WITH_COMPLETION_EXCEPTION;
			if(e instanceof IllegalArgumentException)
				current *= EXIT_WITH_ILLEGAL_ARGUMENT_EXCEPTION;
			return calcExitCode(e.getCause(), current);
		}
}
