package poker.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import exceptions.DeckException;
import netgame.common.Hub;
import pokerBase.Action;
import pokerBase.Card;
import pokerBase.CardDraw;
import pokerBase.Deck;
import pokerBase.GamePlay;
import pokerBase.GamePlayPlayerHand;
import pokerBase.Player;
import pokerBase.Rule;
import pokerBase.Table;
import pokerEnums.eAction;
import pokerEnums.eCardDestination;
import pokerEnums.eDrawCount;
import pokerEnums.eGame;
import pokerEnums.eGameState;

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;
	private eGameState eGameState;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	protected void messageReceived(int ClientID, Object message) {

		if (message instanceof Action) {
			Player actPlayer = (Player) ((Action) message).getPlayer();
			Action act = (Action)message;
			switch (act.getAction())
			{
			case Sit:
				HubPokerTable.AddPlayerToTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);				
				break;
			case Leave:
				HubPokerTable.RemovePlayerFromTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);				
				break;
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);				
				break;
			case StartGame:
				//	TODO: Set HubGamePlay = new instance of GamePlay
				
				Rule rle = new Rule(act.geteGame());
				
				//	TODO: - Finish this code 
				//			HubGamePlay = new GamePlay(<parm>,<parm>,<parm>);
				HubGamePlay = new GamePlay(rle,act.getPlayer().getPlayerID());

				//	DO NOT BREAK... let it fall through to Draw so it will draw
				//	the first cards of the game
				
				//	TODO: Add the players to the game based on who's sitting at the table
				//			Call 'setGamePlayers' in GamePlay
				
				//	TODO: Pick a random player to be the dealer (between players playing)
				
				Player dealer = HubPokerTable.PickRandomPlayerAtTable();
				
				//	TODO: Set the deck in HubGamePlay based on game's rule set
				
				Deck tempdeck = new Deck(rle.GetNumberOfJokers(),rle.GetWildCards());
				HubGamePlay.setGameDeck(tempdeck);
				HubGamePlay = new GamePlay(rle, dealer.getPlayerID());
				HubGamePlay.setGamePlayers(HubPokerTable.getHashPlayers());
				
				//	TODO: 
			case Draw:
				Rule rule = HubGamePlay.getRule();
				/**Iterator it = getHashPlayers().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					Player p = (Player)pair.getValue();
					if (p.getiPlayerPosition() == iPlayerPosition)
						pl = p;
				}*/
				/**int [] order = new int [4];
				order = HubGamePlay.GetOrder(HubGamePlay.getGamePlayers().get(HubGamePlay.getGameDealer()).getiPlayerPosition());
				int currentPlayer = order[0];
				for(int i = 1; i <= rule.getTotalCardsToDraw(); i++){
					CardDraw cd = rule.GetDrawCard(eDrawCount.geteDrawCount(i));
					if (cd.getCardDestination() == eCardDestination.Community){
						try {
							HubGamePlay.addCardtoCommunity(HubGamePlay.getGameDeck().Draw());
						} catch (DeckException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						
						try {
							HubGamePlay.getPlayerHand(HubGamePlay.getPlayerByPosition(currentPlayer)).AddToCardsInHand(HubGamePlay.getGameDeck().Draw());
							currentPlayer = HubGamePlay.NextPosition(currentPlayer, order);
						} catch (DeckException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						};
						
					}
				}
				*/
				
				//	TODO: Draw cards based on next in hmCardDraw
				//			You might have to draw two cards, one card, three cards
				
				//			You might have to add cards to player(s) hands, community

				//	TODO: Update eDrawCountLast in GamePlay.  This attribute will 
				//		tell the client what card(s) need to be dealt to which players.
				HubGamePlay.seteDrawCountLast(eDrawCount.geteDrawCount(rule.getTotalCardsToDraw()));
				resetOutput();
				
				sendToAll(HubGamePlay);	
				break;
			}			
		}

	}

}
