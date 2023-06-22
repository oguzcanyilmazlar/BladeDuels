package me.acablade.bladeduels.exception;

import revxrsal.commands.command.CommandParameter;
import revxrsal.commands.exception.InvalidValueException;

public class InvalidKitException extends InvalidValueException {
    public InvalidKitException(CommandParameter parameter, String input) {
        super(parameter, input);
    }
}
