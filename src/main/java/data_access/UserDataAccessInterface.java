package data_access;

import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;
import use_case.viewhistory.ViewHistoryUserDataAccessInterface;
import use_case.undovisit.UndoVisitUserDataAccessInterface;
import use_case.viewprogress.ViewProgressUserDataAccessInterface;

/**
 * Composite Data Access Interface.
 * Extends multiple use-case-specific interfaces to provide a unified
 * interface for data access operations. This follows the Interface
 * Segregation Principle by composing specific interfaces rather than
 * creating one large interface with all methods.
 */
public interface UserDataAccessInterface extends LoginUserDataAccessInterface,
        SignupUserDataAccessInterface,
        ViewHistoryUserDataAccessInterface,
        UndoVisitUserDataAccessInterface,
        ViewProgressUserDataAccessInterface {
    // Composite interface for easier code writing
}
