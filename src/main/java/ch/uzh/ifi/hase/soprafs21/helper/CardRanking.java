package ch.uzh.ifi.hase.soprafs21.helper;

import ch.uzh.ifi.hase.soprafs21.constant.Rank;
import ch.uzh.ifi.hase.soprafs21.constant.Suit;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.game.cards.Card;

import java.util.ArrayList;
import java.util.Arrays;

public class CardRanking {

    public static ArrayList<UserDraw> getRanking(ArrayList<UserDraw> users) {
        ArrayList<UserDraw> ranking = new ArrayList<>();
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
        private ArrayList<Card> finalCards; //5 best cards

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
            /*if (isStraightFlush()) {
                combination = Combination.STRAIGHT_FLUSH;
                return;
            }*/
            if (isFourOfAKind()) {
                combination = Combination.FOUR_OF_A_KIND;
            }
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

        /*private boolean isStraightFlush() {
            if (isStraight()) {
            }
        }*/

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

    }
}
