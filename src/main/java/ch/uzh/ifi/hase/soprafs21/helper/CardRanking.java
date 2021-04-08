package ch.uzh.ifi.hase.soprafs21.helper;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.cards.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CardRanking {

    public ArrayList<UserDraw> getRanking(GameEntity game) {
        ArrayList<UserDraw> ranking = new ArrayList<>();
        ArrayList<UserDraw> unsorted = new ArrayList<>();
        HashMap<User, UserCombination> usersAndCombination = new HashMap();

        ArrayList<User> activeUsers = game.getActiveUsers();
        for(User user: activeUsers) {
            usersAndCombination.put(user, new UserCombination(user, game.getRiver().getCards()));
        }

        //all users that have a hand as good as others are collected in a UserDraw and added to the unsorted ranking list
        for(User user: activeUsers) {
            UserDraw usersAsGood = new UserDraw();
            usersAsGood.addUser(user, game.getPot().getUserContributionOfAUser(user));
            ArrayList<User> otherUsers = (ArrayList<User>) activeUsers.clone();
            otherUsers.remove(user);
            for(User other: otherUsers) {
                if(usersAndCombination.get(user).asGood(usersAndCombination.get(other))) {
                    usersAsGood.addUser(other, game.getPot().getUserContributionOfAUser(other));
                }
            }
            unsorted.add(usersAsGood);
        }

        ranking.add(unsorted.get(0));
        for(int i = 1; i < unsorted.size(); i++) {
            User user = null;
            for(User u: unsorted.get(i).getUsers()) {
                user = u;
                break;
            }
            for(UserDraw userDrawToCompare: ranking) {
                User userToCompare = null;
                for(User u: userDrawToCompare.getUsers()) {
                    userToCompare = u;
                    break;
                }
                if(usersAndCombination.get(user).isBetterThan(usersAndCombination.get(userToCompare))) {
                    int index = ranking.indexOf(userDrawToCompare);
                    ranking.set(index, unsorted.get(i));
                    ranking.add(userDrawToCompare);
                    break;
                }
            }
            if(!ranking.contains(unsorted.get(i))) {
               ranking.add(unsorted.get(i));
            }

        }

        return ranking;
    }

    private enum Combination {
        ROYAL_FLUSH,
        STRAIGHT_FLUSH,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        FLUSH,
        STRAIGHT,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }

    private class UserCombination {
        private User user;
        private Combination combination;
        private ArrayList<Card> cards;
        private ArrayList<Card> finalCards = new ArrayList<>(); //5 best cards

        UserCombination(User user, ArrayList<Card> river) {
            this.user = user;
            cards = (ArrayList<Card>) river.clone();
            cards.addAll(Arrays.asList(user.getCards()));
            calcCombination();
        }

        public Combination getCombination() {
            return combination;
        }

        private void calcCombination() {
            if (isRoyalFlush()) {
                combination = Combination.ROYAL_FLUSH;
                return;
            }
            if (isStraightFlush()) {
                combination = Combination.STRAIGHT_FLUSH;
                return;
            }
            if (isFourOfAKind()) {
                combination = Combination.FOUR_OF_A_KIND;
                return;
            }
            if(isFullHouse()) {
                combination = Combination.FULL_HOUSE;
                return;
            }
            if(isFlush()) {
                combination = Combination.FLUSH;
                return;
            }
            if(isStraight()) {
                combination = Combination.STRAIGHT;
                return;
            }
            if(isThreeOfAKind()) {
                combination = Combination.THREE_OF_A_KIND;
                return;
            }
            if(isTwoPair()) {
                combination = Combination.TWO_PAIR;
                return;
            }
            if(isPair()) {
                combination = Combination.ONE_PAIR;
                return;
            }
            combination = Combination.HIGH_CARD;
            setFinalCardsForHighCard();
            return;

        }

        private boolean isRoyalFlush() {
            if (isFlush()) {
                ArrayList<Rank> ranks = new ArrayList<>();
                for (Card card : finalCards) {
                    ranks.add(card.getRank());
                }
                for (Rank rank : Rank.values()) {
                    if (!ranks.contains(rank)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        private boolean isStraightFlush() {
            if (isFlush()) {
                Suit suit = finalCards.get(0).getSuit();
                ArrayList<Card> highEnds = new ArrayList<>();
                ArrayList<Integer> cardRanks = new ArrayList<>();
                for(Card card: this.cards) {
                    cardRanks.add(card.getRank().ordinal());
                }
                for(Card card: this.cards) {
                    boolean isStraightFlush = true;
                    int rank = card.getRank().ordinal();
                    for(int i = 1; i <= 4; i++) {
                        int expectedRank = rank - i;
                        if(!cardRanks.contains(expectedRank) || !(card.getSuit() == suit)) {
                            isStraightFlush = false;
                        }
                    }
                    if(isStraightFlush) {
                        highEnds.add(card);
                    }
                }
                if(highEnds.isEmpty()) {
                    return false;
                }
                else {
                    setFinalCardsForStraightFlush(suit, getHighestCard(highEnds).getRank().ordinal());
                    return true;
                }
            }
            return false;
        }

        private void setFinalCardsForStraightFlush(Suit suit, int rankOfHighest) {
            for(int i = 0; i < 5; i++) {
                int expectedRank = rankOfHighest - i;
                for(Card card: this.cards) {
                    if(card.getRank().ordinal() == expectedRank && card.getSuit() == suit) {
                        finalCards.add(card);
                    }
                }
            }
        }

        private boolean isFourOfAKind() {
            int count = 0;
            for (Rank rank : Rank.values()) {
                for (Card card : cards) {
                    if (card.getRank() == rank) {
                        count++;
                    }
                }
                if (count >= 4) {
                    setFinalCardsForFourOfAKind(rank);
                    return true;
                }
                count = 0;
            }
            return false;
        }

        private void setFinalCardsForFourOfAKind(Rank rank) {
            ArrayList<Card> cards = new ArrayList<>();
            for (Card card : this.cards) {
                if (card.getRank() == rank) {
                    cards.add(card);
                }
            }
            ArrayList<Card> cardsWithoutFour = (ArrayList<Card>) this.cards.clone();
            cardsWithoutFour.removeAll(cards);
            cards.add(getHighestCard(cards));
            finalCards = cards;
        }

        private boolean isFullHouse() {
            Rank rankOfThrees = null;
            Rank rankOfPair = null;
            boolean hasThreeOfAKind = false;
            boolean hasPair = false;
            int count = 0;
            for (Rank rank : Rank.values()) {
                for (Card card : cards) {
                    if (card.getRank() == rank) {
                        count++;
                    }
                }
                if (count >= 3) {
                    //check if three of a kind were detected before, since they could become the highest pair
                    if (hasThreeOfAKind) {
                        if (rankOfPair == null || rankOfPair.ordinal() < rankOfThrees.ordinal()) {
                            rankOfPair = rankOfThrees;
                            hasPair = true;
                        }
                        rankOfThrees = rank;
                        hasThreeOfAKind = true;
                    }
                    else if (count == 2) {
                        rankOfPair = rank;
                        hasPair = true;
                    }
                }
            }
            if(!hasThreeOfAKind || !hasPair) {
                return false;
            }
            setFinalCardsForFullHouse(rankOfThrees, rankOfPair);
            return true;
        }

        private void setFinalCardsForFullHouse(Rank rankOfThrees, Rank rankOfPair) {
            ArrayList<Card> cards = new ArrayList<>();
            for(Card card : this.cards) {
                int countThree = 0;
                int countPair = 0;
                    if(countThree < 3 && card.getRank() == rankOfThrees) {
                        cards.add(card);
                        countThree ++;
                    } else if(countPair < 2 && card.getRank() == rankOfPair) {
                        cards.add(card);
                        countPair ++;
                    }
            }
        }

        private boolean isFlush() {
            int count = 0;
            for (Suit suit : Suit.values()) {
                for (Card card : cards) {
                    if (card.getSuit() == suit) {
                        count++;
                    }
                }
                if (count >= 5) {
                    setFinalCardsForFlush(suit);
                    return true;
                }
                count = 0;
            }
            return false;
        }

        private void setFinalCardsForFlush(Suit suit) {
            ArrayList<Card> cards = new ArrayList<>();
            for (Card card : this.cards) {
                if (card.getSuit() == suit) {
                    cards.add(card);
                }
            }
            while (cards.size() > 5) {
                removeLowestRankCard(cards);
            }
            finalCards = cards;
        }

        private boolean isStraight() {
            ArrayList<Card> highEnds = new ArrayList<>();
            ArrayList<Integer> cardRanks = new ArrayList<>();
            for(Card card: this.cards) {
                cardRanks.add(card.getRank().ordinal());
            }
            for(Card card: this.cards) {
                boolean isStraight = true;
                int rank = card.getRank().ordinal();
                for(int i = 1; i <= 4; i++) {
                    int expectedRank = rank - i;
                    if(!cardRanks.contains(expectedRank)) {
                        isStraight = false;
                    }
                }
                if(isStraight) {
                    highEnds.add(card);
                }
            }
            if(highEnds.isEmpty()) {
                return false;
            }
            else {
                setFinalCardsForStraight(getHighestCard(highEnds).getRank().ordinal());
                return true;
            }
        }

        private void setFinalCardsForStraight(int rank) {
            for(int i = 0; i <= 4; i++) {
                int expectedRank = rank - i;
                for(Card card: this.cards) {
                    if(card.getRank().ordinal() == expectedRank) {
                        finalCards.add(card);
                        break;
                    }
                }
            }
        }

        private boolean isThreeOfAKind() {
            boolean isThreeOfAKind = false;
            int count = 0;
            for (Rank rank : Rank.values()) {
                for (Card card : cards) {
                    if (card.getRank() == rank) {
                        count++;
                    }
                }
                if (count >= 3) {
                    setFinalCardsForThreeOfAKind(rank);
                    isThreeOfAKind = true;
                }
                count = 0;
            }
            return isThreeOfAKind;

        }

        //sets final cards in order: three, then highest, then second highest
        private void setFinalCardsForThreeOfAKind(Rank rank) {
            for(Card card: this.cards) {
                if(card.getRank() == rank) {
                    finalCards.add(card);
                }
            }
            ArrayList<Card> cards = (ArrayList<Card>) this.cards.clone();
            finalCards.add(getHighestCard(cards));
            cards.remove(getHighestCard(cards));
            finalCards.add(getHighestCard(cards));

        }

        private boolean isTwoPair() {
            ArrayList<Rank> pairs = new ArrayList<>();
            for(Rank rank: Rank.values()) {
                int count = 0;
                for(Card card: this.cards) {
                    if(card.getRank() == rank) {
                        count ++;
                        if(count == 2) {
                            pairs.add(rank);
                            break;
                        }
                    }
                }
            }
            if(pairs.size() >= 2) {
                while(pairs.size() > 2) {
                    removeLowestRank(pairs);
                }
                setFinalCardsForTwoPair(pairs.get(1), pairs.get(0));
                return true;
            }
            return false;
        }

        //sets finalCards in order: high pair, low pair, highest other card
        private void setFinalCardsForTwoPair(Rank high, Rank low) {
            for(Card card: this.cards) {
                if(card.getRank() == high) {
                    finalCards.add(card);
                }
            }
            for(Card card: this.cards) {
                if(card.getRank() == low) {
                    finalCards.add(card);
                }
            }
            ArrayList<Card> cards = (ArrayList<Card>) this.cards.clone();
            boolean oneMore = true;
            while(oneMore) {
                Card card = getHighestCard(cards);
                cards.remove(card);
                if(!finalCards.contains(card)) {
                    finalCards.add(card);
                    oneMore = false;
                }
            }
        }

        private boolean isPair() {
            ArrayList<Rank> pairs = new ArrayList<>();
            for(Rank rank: Rank.values()) {
                int count = 0;
                for(Card card: this.cards) {
                    if(card.getRank() == rank) {
                        count ++;
                        if(count == 2) {
                            pairs.add(rank);
                            break;
                        }
                    }
                }
            }
            if(pairs.size() >= 1) {
                while(pairs.size() >1) {
                    removeLowestRank(pairs);
                }
                setFinalCardsForPair(pairs.get(0));
                return true;
            }
            return false;
        }

        private void setFinalCardsForPair(Rank rank) {
            for(Card card: this.cards) {
                if(card.getRank() == rank) {
                    finalCards.add(card);
                }
            }
            ArrayList<Card> cards = (ArrayList<Card>) this.cards.clone();
            int cardsNeeded = 3;
            while(cardsNeeded > 0) {
                Card card = getHighestCard(cards);
                cards.remove(card);
                if(!finalCards.contains(card)) {
                    finalCards.add(card);
                    cardsNeeded --;
                }
            }
        }

        //not ordered
        private void setFinalCardsForHighCard() {
            ArrayList<Card> cards = (ArrayList<Card>) this.cards.clone();
            while(cards.size() > 5) {
                removeLowestRankCard(cards);
            }
            finalCards = cards;
        }

        private Card removeLowestRankCard(ArrayList<Card> cards) {
            Card lowest = cards.get(0);
            for (Card card : cards) {
                if (card.getRank().ordinal() < lowest.getRank().ordinal()) {
                    lowest = card;
                }
            }
            cards.remove(lowest);
            return lowest;
        }

        private Card getHighestCard(ArrayList<Card> cards) {
            Card highest = cards.get(0);
            for (Card card : cards) {
                if (card.getRank().ordinal() > highest.getRank().ordinal()) {
                    highest = card;
                }
            }
            return highest;
        }

        private void removeLowestRank(ArrayList<Rank> ranks) {
            Rank lowest = ranks.get(0);
            for(Rank rank: ranks) {
                if(rank.ordinal() < lowest.ordinal()) {
                    lowest = rank;
                }
            }
            ranks.remove(lowest);
        }

        public boolean isBetterThan(UserCombination other) {
            if(this.combination.ordinal() < other.combination.ordinal()) {
                return true;
            } else if(this.combination.ordinal() > other.combination.ordinal()) {
                return false;
            } else {
                //this case is when they have the same combo and the individual cards decide, to be implemented
                return false;
            }
        }

        //to be implemented
        public boolean asGood(UserCombination other) {
            return false;
        }
    }
}
