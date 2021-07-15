package blackjack.controller;

import blackjack.model.*;
import blackjack.view.Input;
import blackjack.view.Output;

import java.util.Arrays;
import java.util.List;

public class BlackJackController {
    public static final int BURST_COUNT = 21;
    private static final int FIRST_CARD_INDEX = 0;
    private static final int STARTING_CARD_COUNT = 2;

    private static final String SPLIT_SEPARATOR = ",";
    private static final String GET_RECEIVE_CARD = "y";

    private static final CardGenerator cardGenerator = new CardGenerator();

    public void blankJackStart() {
        Player dealer = PlayerFactory.of(PlayerFactory.DEALER);
        List<String> userNames = Arrays.asList(Input.inputNames().split(SPLIT_SEPARATOR));
        Players users = new Players(userNames);

        initialGame(dealer, users);
        startGameLogic(dealer, users);
        progressPlayersTurnLogic(dealer, users);
        showPlayersCardInformationLogic(dealer, users);
        makeWinningStateLogic(dealer, users);

        GameResult gameResult = new GameResult(dealer,users);
        Output.printResult(gameResult.makeResultLogic(dealer, users));
    }

    private void initialGame(Player dealer, Players users) {
        initPlayer(dealer);
        initUsers(users.getUser());
        Output.printInitMessage(users.getUserNames());

    }

    private void initPlayer(Player player) {
        player.addSeveralCard(cardGenerator.getSeveralCard(STARTING_CARD_COUNT));
    }

    private void initUsers(List<Player> users) {
        for (Player user : users) {
            initPlayer(user);
        }
    }

    private void startGameLogic(Player dealer, Players users) {
        Output.printDealerFirstCardName(dealer.getName(), dealer.getCardNames().get(FIRST_CARD_INDEX));
        showUsersCardNames(users);
    }

    private void showUsersCardNames(Players users) {
        for (Player user : users.getUser()) {
            Output.printCardNames(user);
        }
    }

    private void progressPlayersTurnLogic(Player dealer, Players users) {
        doUsersTurn(users);
        doDealerTurn(dealer);
    }

    private void doUsersTurn(Players users) {
        for (Player user : users.getUser()) {
            doUserTurn(user);
        }
    }

    private void doDealerTurn(Player dealer) {
        while (dealer.canReceiveCard()) {
            dealer.addCard(cardGenerator.getOneCard());
            Output.printDealerReceiveCard();
        }

    }

    private void doUserTurn(Player user) {
        boolean userTurn = true;

        while (userTurn) {
            userTurn = isUserTurn(user);
        }
    }

    private boolean isUserTurn(Player user) {
        if (user.canReceiveCard()) {
            return isReceiveCard(user);
        }

        return false;
    }

    private boolean isReceiveCard(Player user) {
        if (Input.inputReceiveCardAnswer(user.getName()).equals(GET_RECEIVE_CARD)) {
            user.addCard(cardGenerator.getOneCard());
            Output.printCardNames(user);
            return true;
        }

        return false;
    }

    private void showPlayersCardInformationLogic(Player dealer, Players users) {
        Output.printPlayerCardInformation(dealer);
        showUsersCardInformation(users);
    }

    private void showUsersCardInformation(Players users) {
        for (Player user : users.getUser()) {
            Output.printPlayerCardInformation(user);
        }
    }

    private void makeWinningStateLogic(Player dealer, Players users) {
        for (Player user : users.getUser()) {
            makeWinningState(dealer, user);
        }
    }

    private void makeWinningState(Player dealer, Player user) {
        if (isDealerBurst(dealer)) {
            dealerLoseLogic(dealer, user);
            return;
        }

        if (isUserBurst(user)) {
            userLoseLogic(dealer, user);
            return;
        }

        comparePlayersLogic(dealer, user);
    }

    private boolean isDealerBurst(Player dealer) {
        return burstCheck(dealer);
    }

    private boolean isUserBurst(Player user) {
        return burstCheck(user);
    }

    private void dealerLoseLogic(Player dealer, Player user) {
        if (!burstCheck(user)) {
            dealer.getWinningState().plusLoseCount();
            user.getWinningState().plusWinCount();
        }
    }

    private void userLoseLogic(Player dealer, Player user) {
        dealer.getWinningState().plusWinCount();
        user.getWinningState().plusLoseCount();
    }

    private void comparePlayersLogic(Player dealer, Player user) {
        if (dealer.getCardValueSum() > user.getCardValueSum()) {
            dealer.getWinningState().plusWinCount();
            user.getWinningState().plusLoseCount();
        }
        if (dealer.getCardValueSum() < user.getCardValueSum()) {
            dealer.getWinningState().plusLoseCount();
            user.getWinningState().plusWinCount();
        }
    }

    private boolean burstCheck(Player player) {
        return player.getCardValueSum() > BURST_COUNT;
    }

}
