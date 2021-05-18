package ch.uzh.ifi.hase.soprafs21.helper;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;
import ch.uzh.ifi.hase.soprafs21.entity.GameEntity;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.cards.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardRanking {

    public List<UserDraw> getRanking(GameEntity game) {
        List<UserDraw> ranking = new ArrayList<>();
        List<UserDraw> unsorted = new ArrayList<>();
        HashMap<User, UserCombination> usersAndCombination = new HashMap<>();

        List<User> activeUsers = game.getActiveUsers();
        for (User user : activeUsers) {
            usersAndCombination.put(user, new UserCombination(user, game.getRiver().getCards()));
        }

        //all users that have a hand as good as others are collected in a UserDraw and added to the unsorted ranking list
        collectInUserDrawAndAddToList(unsorted, activeUsers, game, usersAndCombination);

        //eliminate duplicates
        unsorted = eliminateDuplicates(unsorted);

        ranking.add(unsorted.get(0));
        for (var i = 1; i < unsorted.size(); i++) {
            User user = null;
            for (User u : unsorted.get(i).getUsers()) {
                user = u;
                break;
            }
            for (UserDraw userDrawToCompare : ranking) {
                User userToCompare = null;
                for (User u : userDrawToCompare.getUsers()) {
                    userToCompare = u;
                    break;
                }
                if (usersAndCombination.get(user).isBetterThan(usersAndCombination.get(userToCompare))) {
                    int index = ranking.indexOf(userDrawToCompare);
                    ranking.add(index, unsorted.get(i));
                    break;
                }
            }
            if (!ranking.contains(unsorted.get(i))) {
                ranking.add(unsorted.get(i));
            }
        }

        return ranking;
    }

    private List<UserDraw> eliminateDuplicates(List<UserDraw> unsorted) {
        ArrayList<UserDraw> noDuplicates = new ArrayList<>();
        for (UserDraw userDraw : unsorted) {
            var add = true;
            for (UserDraw ud : noDuplicates) {
                if (userDraw.equals(ud)) {
                    add = false;
                }
            }
            if (add) {
                noDuplicates.add(userDraw);
            }
        }
        return noDuplicates;
    }

    private void collectInUserDrawAndAddToList(List<UserDraw> unsorted, List<User> activeUsers, GameEntity game, HashMap<User, UserCombination> usersAndCombination) {
        for (User user : activeUsers) {
            var usersAsGood = new UserDraw();
            usersAsGood.addUser(user, game.getPot().getUserContributionOfAUser(user));
            List<User> otherUsers = new ArrayList<>(activeUsers);
            otherUsers.remove(user);
            for (User other : otherUsers) {
                if (usersAndCombination.get(user).asGood(usersAndCombination.get(other))) {
                    usersAsGood.addUser(other, game.getPot().getUserContributionOfAUser(other));
                }
            }
            unsorted.add(usersAsGood);
        }
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

        private Combination combination;
        private ArrayList<Card> cards;
        private ArrayList<Card> finalCards = new ArrayList<>(); //5 best cards

        UserCombination(User user, List<Card> river) {
            cards = new ArrayList<>(river);
            cards.addAll(user.getCards());
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
            if (isFullHouse()) {
                combination = Combination.FULL_HOUSE;
                return;
            }
            if (isFlush()) {
                combination = Combination.FLUSH;
                return;
            }
            if (isStraight()) {
                combination = Combination.STRAIGHT;
                return;
            }
            if (isThreeOfAKind()) {
                combination = Combination.THREE_OF_A_KIND;
                return;
            }
            if (isTwoPair()) {
                combination = Combination.TWO_PAIR;
                return;
            }
            if (isPair()) {
                combination = Combination.ONE_PAIR;
                return;
            }
            combination = Combination.HIGH_CARD;
            setFinalCardsForHighCard();

        }

        private boolean isRoyalFlush() {
            if (isFlush()) {
                ArrayList<Rank> ranks = new ArrayList<>();
                for (Card card : finalCards) {
                    ranks.add(card.getRank());
                }
                ArrayList<Rank> ranksInRoyal = new ArrayList<>();
                ranksInRoyal.addAll(List.of(
                        Rank.ACE,
                        Rank.KING,
                        Rank.QUEEN,
                        Rank.JACK,
                        Rank.TEN
                ));
                for (Rank rank : ranksInRoyal) {
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
                var suit = finalCards.get(0).getSuit();
                ArrayList<Card> highEnds = new ArrayList<>();
                ArrayList<Integer> cardRanks = new ArrayList<>();
                for (Card card : this.cards) {
                    cardRanks.add(card.getRank().ordinal());
                }
                isStraightFlushHandler(cardRanks, suit, highEnds);

                if (highEnds.isEmpty()) {
                    return false;
                }
                else {
                    setFinalCardsForStraightFlush(suit, getHighestCard(highEnds).getRank().ordinal());
                    return true;
                }
            }
            return false;
        }

        private void isStraightFlushHandler(ArrayList<Integer> cardRanks, Suit suit, ArrayList<Card> highEnds){
            for (Card card : this.cards) {
                var isStraightFlush = true;
                int rank = card.getRank().ordinal();
                for (var i = 1; i <= 4; i++) {
                    int expectedRank = rank - i;
                    if (!cardRanks.contains(expectedRank) || card.getSuit() != suit) {
                        isStraightFlush = false;
                        break;
                    }
                }
                if (isStraightFlush) {
                    highEnds.add(card);
                }
            }
        }


        private void setFinalCardsForStraightFlush(Suit suit, int rankOfHighest) {
            for (var i = 0; i < 5; i++) {
                int expectedRank = rankOfHighest - i;
                for (Card card : this.cards) {
                    if (card.getRank().ordinal() == expectedRank && card.getSuit() == suit) {
                        finalCards.add(card);
                    }
                }
            }
        }

        private boolean isFourOfAKind() {
            var count = 0;
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
            ArrayList<Card> cardList = new ArrayList<>();
            for (Card card : this.cards) {
                if (card.getRank() == rank) {
                    cardList.add(card);
                }
            }
            ArrayList<Card> cardsWithoutFour = new ArrayList<>(this.cards);
            cardsWithoutFour.removeAll(cardList);
            cardList.add(getHighestCard(cardList));
            finalCards = cardList;
        }

        private boolean isFullHouse() {
            Rank rankOfThrees = null;
            Rank rankOfPair = null;
            var hasThreeOfAKind = false;
            var hasPair = false;
            var count = 0;
            for (Rank rank : Rank.values()) {
                count = 0;
                count = fullHouseCounter(count, rank);
                if (count >= 3) {
                    //check if three of a kind were detected before, since they could become the highest pair
                    if (hasThreeOfAKind && (rankOfPair == null || rankOfPair.ordinal() < rankOfThrees.ordinal())) {
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
            if (!hasThreeOfAKind || !hasPair) {
                return false;
            }
            setFinalCardsForFullHouse(rankOfThrees, rankOfPair);
            return true;
        }

        private int fullHouseCounter(int count, Rank rank){
            for (Card card : cards) {
                if (card.getRank() == rank) {
                    count++;
                }
            }
            return count;
        }


        private void setFinalCardsForFullHouse(Rank rankOfThrees, Rank rankOfPair) {
            ArrayList<Card> cardList = new ArrayList<>();
            var countThree = 0;
            var countPair = 0;
            for (Card card : this.cards) {
                if (countThree < 3 && card.getRank() == rankOfThrees) {
                    cardList.add(card);
                    countThree++;
                }
                else if (countPair < 2 && card.getRank() == rankOfPair) {
                    cardList.add(card);
                    countPair++;
                }
            }
            finalCards = cardList;
        }

        private boolean isFlush() {
            var count = 0;
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
            ArrayList<Card> cardList = new ArrayList<>();
            for (Card card : this.cards) {
                if (card.getSuit() == suit) {
                    cardList.add(card);
                }
            }
            while (cardList.size() > 5) {
                removeLowestRankCard(cardList);
            }
            finalCards = cardList;
        }

        private boolean isStraight() {
            ArrayList<Card> highEnds = new ArrayList<>();
            ArrayList<Integer> cardRanks = new ArrayList<>();
            for (Card card : this.cards) {
                cardRanks.add(card.getRank().ordinal());
            }
            for (Card card : this.cards) {
                var isStraight = true;
                int rank = card.getRank().ordinal();
                for (var i = 1; i <= 4; i++) {
                    int expectedRank = rank - i;
                    if (!cardRanks.contains(expectedRank)) {
                        isStraight = false;
                    }
                }
                if (isStraight) {
                    highEnds.add(card);
                }
            }
            if (highEnds.isEmpty()) {
                return false;
            }
            else {
                setFinalCardsForStraight(getHighestCard(highEnds).getRank().ordinal());
                return true;
            }
        }

        private void setFinalCardsForStraight(int rank) {
            for (var i = 0; i <= 4; i++) {
                int expectedRank = rank - i;
                for (Card card : this.cards) {
                    if (card.getRank().ordinal() == expectedRank) {
                        finalCards.add(card);
                        break;
                    }
                }
            }
        }

        private boolean isThreeOfAKind() {
            var isThreeOfAKind = false;
            var count = 0;
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
            for (Card card : this.cards) {
                if (card.getRank() == rank) {
                    finalCards.add(card);
                }
            }
            ArrayList<Card> cardList = new ArrayList<>(this.cards);
            finalCards.add(getHighestCard(cardList));
            cardList.remove(getHighestCard(cardList));
            finalCards.add(getHighestCard(cardList));

        }

        private boolean isTwoPair() {
            ArrayList<Rank> pairs = new ArrayList<>();
            for (Rank rank : Rank.values()) {
                var count = 0;
                for (Card card : this.cards) {
                    if (card.getRank() == rank) {
                        count++;
                        if (count == 2) {
                            pairs.add(rank);
                            break;
                        }
                    }
                }
            }
            if (pairs.size() >= 2) {
                while (pairs.size() > 2) {
                    removeLowestRank(pairs);
                }
                setFinalCardsForTwoPair(pairs.get(1), pairs.get(0));
                return true;
            }
            return false;
        }

        //sets finalCards in order: high pair, low pair, highest other card
        private void setFinalCardsForTwoPair(Rank high, Rank low) {
            for (Card card : this.cards) {
                if (card.getRank() == high) {
                    finalCards.add(card);
                }
            }
            for (Card card : this.cards) {
                if (card.getRank() == low) {
                    finalCards.add(card);
                }
            }
            ArrayList<Card> cardList = new ArrayList<>(this.cards);
            var oneMore = true;
            while (oneMore) {
                var card = getHighestCard(cardList);
                cardList.remove(card);
                if (!finalCards.contains(card)) {
                    finalCards.add(card);
                    oneMore = false;
                }
            }
        }

        private boolean isPair() {
            ArrayList<Rank> pairs = new ArrayList<>();
            for (Rank rank : Rank.values()) {
                var count = 0;
                for (Card card : this.cards) {
                    if (card.getRank() == rank) {
                        count++;
                        if (count == 2) {
                            pairs.add(rank);
                            break;
                        }
                    }
                }
            }
            if (!pairs.isEmpty()) {
                while (pairs.size() > 1) {
                    removeLowestRank(pairs);
                }
                setFinalCardsForPair(pairs.get(0));
                return true;
            }
            return false;
        }

        private void setFinalCardsForPair(Rank rank) {
            for (Card card : this.cards) {
                if (card.getRank() == rank) {
                    finalCards.add(card);
                }
            }
            ArrayList<Card> cardList = new ArrayList<>(this.cards);
            var cardsNeeded = 3;
            while (cardsNeeded > 0) {
                var card = getHighestCard(cardList);
                cardList.remove(card);
                if (!finalCards.contains(card)) {
                    finalCards.add(card);
                    cardsNeeded--;
                }
            }
        }

        //not ordered
        private void setFinalCardsForHighCard() {
            ArrayList<Card> cardList = new ArrayList<>(this.cards);
            while (cardList.size() > 5) {
                removeLowestRankCard(cardList);
            }
            finalCards = cardList;
        }

        private Card removeLowestRankCard(ArrayList<Card> cards) {
            var lowest = cards.get(0);
            for (Card card : cards) {
                if (card.getRank().ordinal() < lowest.getRank().ordinal()) {
                    lowest = card;
                }
            }
            cards.remove(lowest);
            return lowest;
        }

        private Card getHighestCard(ArrayList<Card> cards) {
            var highest = cards.get(0);
            for (Card card : cards) {
                if (card.getRank().ordinal() > highest.getRank().ordinal()) {
                    highest = card;
                }
            }
            return highest;
        }

        private void removeLowestRank(ArrayList<Rank> ranks) {
            var lowest = ranks.get(0);
            for (Rank rank : ranks) {
                if (rank.ordinal() < lowest.ordinal()) {
                    lowest = rank;
                }
            }
            ranks.remove(lowest);
        }

        public boolean isBetterThan(UserCombination other) {
            if (this.combination.ordinal() < other.combination.ordinal()) {
                return true;
            }
            else if (this.combination.ordinal() > other.combination.ordinal()) {
                return false;
            }
            else {
                //this case is when they have the same combo and the individual cards decide
                switch (this.combination) {
                    case ROYAL_FLUSH:
                        return false;
                    case STRAIGHT_FLUSH:
                        return straightFlushDecider(other);
                    case FOUR_OF_A_KIND:
                        return fourOfAKindDecider(other);
                    case FULL_HOUSE:
                        return fullHouseDecider(other);
                    case FLUSH:
                        return flushDecider(other);
                    case STRAIGHT:
                        return straightDecider(other);
                    case THREE_OF_A_KIND:
                        return threeOfAKindDecider(other);
                    case TWO_PAIR:
                        return twoPairDecider(other);
                    case ONE_PAIR:
                        return onePairDecider(other);
                    case HIGH_CARD:
                        return highCardDecider(other);
                    default:
                        return false;
                }

            }
        }

        private boolean straightFlushDecider(UserCombination other) {
            return getHighestCard(this.finalCards).getRank().ordinal() > other.getHighestCard(other.finalCards).getRank().ordinal();
        }

        private boolean fourOfAKindDecider(UserCombination other) {
            Rank[] thisRank;
            Rank[] otherRank;
            thisRank = this.getRanksGivenFour(); //length should always be 2
            otherRank = other.getRanksGivenFour(); //length should always be 2
            for (var i = 0; i < thisRank.length; i++) {
                if (thisRank[i].ordinal() > otherRank[i].ordinal()) { //nullPointerException?
                    return true;
                }
                else if (thisRank[i].ordinal() < otherRank[i].ordinal()) {
                    return false;
                }
            }
            return false;
        }

        private boolean fullHouseDecider(UserCombination other) {
            Rank[] thisRank;
            Rank[] otherRank;
            thisRank = this.getRanksGivenFullHouse();
            otherRank = other.getRanksGivenFullHouse();
            for (var i = 0; i < thisRank.length; i++) {
                if (thisRank[i].ordinal() > otherRank[i].ordinal()) {
                    return true;
                }
                else if (thisRank[i].ordinal() < otherRank[i].ordinal()) {
                    return false;
                }
            }
            return false;
        }

        private boolean flushDecider(UserCombination other) {
            ArrayList<Card> thisCards = null;
            ArrayList<Card> otherCards = null;
            thisCards = new ArrayList<>(this.finalCards);
            otherCards = new ArrayList<>(other.finalCards);
            while (!thisCards.isEmpty()) {
                var card = getHighestCard(thisCards);
                thisCards.remove(card);
                var thisCurrentRank = card.getRank();
                card = getHighestCard(otherCards);
                otherCards.remove(card);
                var otherCurrentRank = card.getRank();
                if (thisCurrentRank.ordinal() > otherCurrentRank.ordinal()) {
                    return true;
                }
                else if (thisCurrentRank.ordinal() < otherCurrentRank.ordinal()) {
                    return false;
                }
            }
            return false;
        }

        private boolean straightDecider(UserCombination other) {
            return getHighestCard(this.finalCards).getRank().ordinal() > getHighestCard(other.finalCards).getRank().ordinal();
        }

        private boolean threeOfAKindDecider(UserCombination other) {
            Rank[] thisRank;
            Rank[] otherRank;
            thisRank = this.getRanksGivenThree(); //array length 3
            otherRank = other.getRanksGivenThree(); // array length 3
            for (var i = 0; i < thisRank.length; i++) {
                if (thisRank[i].ordinal() > otherRank[i].ordinal()) {
                    return true;
                }
                else if (thisRank[i].ordinal() < otherRank[i].ordinal()) {
                    return false;
                }
            }
            return false;
        }

        private boolean twoPairDecider(UserCombination other) {
            Rank[] thisRank;
            Rank[] otherRank;
            thisRank = this.getRanksGivenTwoPair(); //array length 3
            otherRank = other.getRanksGivenTwoPair(); // array length 3
            for (var i = 0; i < thisRank.length; i++) {
                if (thisRank[i].ordinal() > otherRank[i].ordinal()) {
                    return true;
                }
                else if (thisRank[i].ordinal() < otherRank[i].ordinal()) {
                    return false;
                }
            }
            return false;
        }

        private boolean onePairDecider(UserCombination other) {
            Rank[] thisRank;
            Rank[] otherRank;
            thisRank = this.getRanksGivenOnePair(); //array length 4
            otherRank = other.getRanksGivenOnePair(); // array length 4
            for (var i = 0; i < thisRank.length; i++) {
                if (thisRank[i].ordinal() > otherRank[i].ordinal()) {
                    return true;
                }
                else if (thisRank[i].ordinal() < otherRank[i].ordinal()) {
                    return false;
                }
            }
            return false;
        }

        private boolean highCardDecider(UserCombination other) {
            Rank[] thisRank;
            Rank[] otherRank;
            thisRank = this.getRanksOrdered();
            otherRank = other.getRanksOrdered();
            for (var i = 0; i < thisRank.length; i++) {
                if (thisRank[i].ordinal() > otherRank[i].ordinal()) {
                    return true;
                }
                else if (thisRank[i].ordinal() < otherRank[i].ordinal()) {
                    return false;
                }
            }
            return false;
        }

        public boolean asGood(UserCombination other) {
            return (this.getCombination() == other.getCombination()) && (!this.isBetterThan(other) && !other.isBetterThan(this));
        }

        /**
         * Assumption: one deck --> five of a kind not possible
         * assuming that this is of combination FOUR_OF_A_KING and its final cards are saved, this returns an array
         * of length 2 with element index 0 being rank of the fours, and element index 1 being the other card
         *
         * @return Rank[2] (Rank[0] being four, Rank[1] being other card)
         */
        private Rank[] getRanksGivenFour() {
            Rank rankOfFour = null;
            Rank rankOther = null;
            for (Rank rank : Rank.values()) {
                var count = 0;
                for (Card card : finalCards) {
                    if (card.getRank() == rank) {
                        count++;
                        if (count >= 4) {
                            rankOfFour = rank;
                            break;
                        }
                    }
                }
                rankOther = addFifthRank(rankOfFour);
            }

            return new Rank[]{rankOfFour, rankOther};
        }

        private Rank addFifthRank(Rank rankOfFour) {
            for (Card card : finalCards) {
                if (card.getRank() != rankOfFour) {
                    return card.getRank();
                }
            }
            throw new IllegalStateException("There should be at least one Card which does not have the same Rank as the other 4!");
        }

        /**
         * assuming that this is of combination FULL_HOUSE and its final cards are saved, this returns an array
         * of length 2 with element index 0 being rank of the three, and element index 1 being the two
         *
         * @return Rank[2] (Rank[0] being three, Rank[1] being two)
         */
        private Rank[] getRanksGivenFullHouse() {
            Rank rankOfThree = null;
            Rank rankOfTwo = null;
            for (var i = 0; i < finalCards.size(); i++) {
                var rank = finalCards.get(i).getRank();
                var count = 0;
                for (Card card : finalCards) {
                    if (card.getRank() == rank) {
                        count++;
                    }
                }
                if (count == 3) {
                    rankOfThree = rank;
                }
                else if (count == 2) {
                    rankOfTwo = rank;
                }
            }
            return new Rank[]{rankOfThree, rankOfTwo};
        }

        /**
         * assuming that this is of combination THREE_OF_A_KIND and its final cards are saved, this returns an array
         * of length 3 with element index 0 being rank of the three, and element index 1 being highest of other, and
         * element index 2 being last rank
         *
         * @return Rank[3] (Rank[0] being three, Rank[1] being highest of other, Rank[2] being last rank)
         */
        private Rank[] getRanksGivenThree() {
            Rank three = null; //rank of three
            Rank rank1 = null; //rank of other card
            Rank rank2 = null; //rank of other card
            for (Rank rank : Rank.values()) {
                var count = 0;
                for (Card card : finalCards) {
                    if (card.getRank() == rank) {
                        count++;
                    }
                }
                if (count >= 3) {
                    three = rank;
                }
                else if (count == 1) {
                    if (rank1 == null) {
                        rank1 = rank;
                    }
                    else {
                        rank2 = rank;
                    }
                }
            }
            if (rank2.ordinal() > rank1.ordinal()) {
                var placeholder = rank1;
                rank1 = rank2;
                rank2 = placeholder;
            }
            return new Rank[]{three, rank1, rank2};
        }

        /**
         * assuming that this is of combination TWO_PAIR and its final cards are saved, this returns an array
         * of length 3 with element index 0 being rank of the higher pair, and element index 1 being other pair, and
         * element index 2 being last rank
         *
         * @return Rank[3] (Rank[0] being high pair, Rank[1] being lower pair, Rank[2] being last rank)
         */
        private Rank[] getRanksGivenTwoPair() {
            Rank pair1 = null; //rank of three
            Rank pair2 = null; //rank of other card
            Rank other = null; //rank of other card
            for (Rank rank : Rank.values()) {
                var count = 0;
                for (Card card : finalCards) {
                    if (card.getRank() == rank) {
                        count++;
                    }
                }
                if (count >= 2) {
                    if (pair1 == null) {
                        pair1 = rank;
                    }
                    else {
                        pair2 = rank;
                    }
                }
                else if (count == 1) {
                    other = rank;
                }
            }
            if (pair2.ordinal() > pair1.ordinal()) {
                Rank placeholder = pair1;
                pair1 = pair2;
                pair2 = placeholder;
            }
            return new Rank[]{pair1, pair2, other};
        }

        /**
         * assuming that this is of combination ONE_PAIR and its final cards are saved, this returns an array
         * of length 4 with element index 0 being rank of pair, and element index 1 to 3 being the other cards,
         * beginning from highest rank
         *
         * @return Rank[4] (Rank[0] being high pair, Rank[1] being lower pair, Rank[2] being last rank)
         */
        private Rank[] getRanksGivenOnePair() {
            var ranks = new Rank[4];
            ArrayList<Card> cardList = new ArrayList<>(finalCards);
            Rank pair = null;
            for (Rank rank : Rank.values()) {
                var count = 0;
                for (Card card : finalCards) {
                    if (card.getRank() == rank) {
                        count++;
                    }
                }
                if (count >= 2) {
                    pair = rank;
                    ranks[0] = pair;
                    break;
                }
            }
            //starts with index 1, since 0 is already in ranks (pair)
            var i = 1;
            while (!cardList.isEmpty()) {
                var card = getHighestCard(cardList);
                cardList.remove(card);
                if (card.getRank() != pair) {
                    ranks[i] = card.getRank();
                    i++;
                }
            }
            return ranks;
        }

        /**
         * assuming that this is of combination HIGH_CARD and its final cards are saved, this returns an array
         * of length 5 with elements being ordered by ranks, ordered from highest to lowest
         *
         * @return Rank[5]
         */
        private Rank[] getRanksOrdered() {
            var ranks = new Rank[5];
            ArrayList<Card> cardList = new ArrayList<>(finalCards);
            for (var i = 0; i < ranks.length; i++) {
                var card = getHighestCard(cardList);
                cardList.remove(card);
                ranks[i] = card.getRank();
            }
            return ranks;
        }

    }
}
