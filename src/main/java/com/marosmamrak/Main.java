package com.marosmamrak;

import com.marosmamrak.command.CommandProcessor;
import com.marosmamrak.repository.UserRepository;
import com.marosmamrak.repository.UserRepositoryImpl;
import com.marosmamrak.util.DatabaseInitializer;
import com.marosmamrak.util.DatabaseUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DatabaseUtils.getConnection()) {

            DatabaseInitializer.createUsersTable(connection);

            UserRepository userRepository = new UserRepositoryImpl(connection);
            BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
            ExecutorService executorService = Executors.newFixedThreadPool(1);

            // Create a semaphore with 0 permits to initially block the command processor thread
            Semaphore semaphore = new Semaphore(0);

            // Start the command processor
            Thread commandProcessorThread = startCommandProcessor(commandQueue, userRepository, semaphore);

            // Wait for the command processor thread to start
            semaphore.acquire();

            // Adding commands to the queue
            addCommandsToQueue(commandQueue);

            // Stop the command processor
            commandQueue.put("STOP");
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("ExecutorService did not terminate within the specified timeout.");
            }

            // Wait for the command processor thread to finish
            commandProcessorThread.join();
        } catch (SQLException | InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    static Thread startCommandProcessor(BlockingQueue<String> commandQueue, UserRepository userRepository, Semaphore semaphore) {
        CommandProcessor commandProcessor = new CommandProcessor(commandQueue, userRepository, semaphore);
        Thread commandProcessorThread = new Thread(commandProcessor);
        commandProcessorThread.start();
        return commandProcessorThread; // Return the Thread object, not the CommandProcessor
    }


    private static void addCommandsToQueue(BlockingQueue<String> commandQueue) throws InterruptedException {
        commandQueue.put("ADD 1 a1 Robert");
        commandQueue.put("ADD 2 a2 Martin");
        commandQueue.put("PRINT_ALL");
        commandQueue.put("DELETE_ALL");
        commandQueue.put("PRINT_ALL");
    }

}
