package com.marosmamrak.command;


import com.marosmamrak.repository.UserRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class CommandProcessor implements Runnable {

    Logger logger = Logger.getLogger(CommandProcessor.class.getName());
    private final BlockingQueue<String> commandQueue;
    private final UserRepository userRepository;

    private final Semaphore semaphore;

    public CommandProcessor(BlockingQueue<String> commandQueue, UserRepository userRepository, Semaphore semaphore) {
        this.commandQueue = commandQueue;
        this.userRepository = userRepository;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            // Release the semaphore to indicate that the command processor thread has started
            semaphore.release();

            while (true) {
                String command = commandQueue.take();
                if (command.equals(UserCommand.STOP.name())) {
                    break;
                }
                processCommand(command);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Interrupted while processing command: " + e.getMessage());
        }
    }

    private synchronized void processCommand(String command) {
        String trimmedCommand = command.trim();
        if (trimmedCommand.isEmpty()) {
            logger.warning("Invalid command: " + command);
            return;
        }

        // Check if the command is "Stop"
        if (trimmedCommand.equalsIgnoreCase("STOP")) {
            return; // Stop processing
        }

        // Split the command into action and parameters
        String[] parts = trimmedCommand.split("\\s+");
        if (parts.length < 1) {
            logger.warning("Invalid command format: " + command);
            return;
        }

        String actionString = parts[0];
        String[] parameters = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        try {
            UserCommand action = UserCommand.valueOf(actionString.toUpperCase());
            switch (action) {
                case ADD:
                    if (parameters.length != 3) {
                        logger.warning("Invalid parameters for 'Add' command: " + command);
                        return;
                    }
                    addUser(parameters);
                    break;
                case PRINT_ALL:
                    if (parameters.length != 0) {
                        logger.warning("Invalid parameters for 'PrintAll' command: " + command);
                        return;
                    }
                    printAllUsers();
                    break;
                case DELETE_ALL:
                    if (parameters.length != 0) {
                        logger.warning("Invalid parameters for 'DeleteAll' command: " + command);
                        return;
                    }
                    deleteAllUsers();
                    break;
                default:
                    logger.warning("Unknown command: " + command);
            }
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid command: " + command);
        }
    }





    private void addUser(String[] parameters) {
        try {
            int userId = Integer.parseInt(parameters[0]);
            if (userId <= 0) {
                logger.warning("Invalid user ID: " + parameters[0]);
                return;
            }
            String userGuid = parameters[1];
            String userName = parameters[2];
            userRepository.addUser(userId, userGuid, userName);
            logger.info("User added successfully.");
        } catch (NumberFormatException e) {
            logger.warning("Invalid user ID: " + parameters[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.warning("Invalid parameters for 'Add' command.");
        } catch (SQLException e) {
            logger.warning("Error adding user to the database: " + e.getMessage());
        }
    }


    private void printAllUsers() {
        try {
            userRepository.getAllUsers().forEach(user -> logger.info(user.toString()));
        } catch (SQLException e) {
            // Log the exception if needed
            logger.severe("Error fetching all users: " + e.getMessage());
        }
    }

    private void deleteAllUsers() {
        try {
            userRepository.deleteAllUsers();
            logger.info("All users deleted.");
        } catch (SQLException e) {
            // Log the exception
            logger.severe("Error deleting all users: " + e.getMessage());
        }
    }
}
