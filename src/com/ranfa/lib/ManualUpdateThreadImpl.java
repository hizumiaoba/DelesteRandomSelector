package com.ranfa.lib;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ranfa.lib.concurrent.CountedThreadFactory;
import com.ranfa.lib.database.Scraping;
import com.ranfa.lib.database.Song;

public class ManualUpdateThreadImpl implements Runnable {

	//Declare flag
	private static boolean flag = true;

	//Declare Executor service
	private Executor executor = Executors.newCachedThreadPool(new CountedThreadFactory(() -> "DRS", "ManualUpdateThread"));
	private BiConsumer<List<Song>, List<Song>> updateConsumer = (list1, list2) -> {
		this.logger.info("Checking database updates...");
		if(list1.size() > list2.size()) {
			long time = System.currentTimeMillis();
			this.logger.info("{} Update detected.", (list1.size() - list2.size()));
			Scraping.writeToJson(list1);
			this.logger.info("Update completed in {} ms", (System.currentTimeMillis() - time));
			this.logger.info("Updated database size: {}", list1.size());
		} else {
			this.logger.info("database is up-to-date.");
		}
	};

	//Declare logger
	private Logger logger = LoggerFactory.getLogger(ManualUpdateThreadImpl.class);

	public ManualUpdateThreadImpl() {
		this.logger.info("ManualUpdateThread is now available.");
	}

	@Override
	public void run() {
		if (flag) {
			flag = false;
		}
		this.logger.info("Checking database updates...");
		CompletableFuture<List<Song>> webData = CompletableFuture.supplyAsync(Scraping::getWholeData, this.executor);
		CompletableFuture<List<Song>> localData = CompletableFuture.supplyAsync(Scraping::getFromJson, this.executor);
		CompletableFuture<Void> afterUpdateFuture = webData.thenAcceptBothAsync(localData, this.updateConsumer, this.executor);
		afterUpdateFuture.whenCompleteAsync((ret, e) -> {
			if(e != null) {
				this.logger.warn("Manual update process has been ended with exception.", e);
			} else {
				this.logger.info("Manual update process has been ended successfully.");
				flag = true;
			}

		}, this.executor);
	}

	public boolean getFlag() {
		return flag;
	}
}
