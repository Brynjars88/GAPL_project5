package GAPL_project4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import javafx.util.Pair;

public class Tester {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{

		String prefix = System.getProperty("user.dir") + "/src/main/java/GAPL_project3/";
		StateMachine sm = raveUtils.getGameSM(prefix + "tictactoeXwin.txt");

		MachineState init = sm.getInitialState();

		long playTime;
		long maxTime = 10000;
		long minTime = 100;

		mastTree treeX = new mastTree(init, null, sm);
		mastTree treeO = new mastTree(init, null, sm);

		Role xplayer = treeX.getRoles().get(0);
		Role oplayer = treeX.getRoles().get(1);
		Pair<Move,mastTree> p;

		int maxIter = Integer.MAX_VALUE;

		boolean xPlaying = true;

		List<Move> theJM;

		while(!sm.isTerminal(treeX.getState())) {
			theJM = new ArrayList<>();

			if(xPlaying) {
				playTime = maxTime;
			} else {
				playTime = minTime;
			}

			System.out.println(treeX.getState().toString());

			p = mastUtils.MCTS(treeX, sm, xplayer, maxIter, System.currentTimeMillis() + playTime, 100);
			treeX = p.getValue();
			theJM.add(p.getKey());
			System.out.println("Legal moves: "+Arrays.toString(treeX.getLegalMoves()[0]));
			System.out.println("Q scores: "+Arrays.toString(treeX.getAllQScores()[0]));
			System.out.println("N scores: "+Arrays.toString(treeX.getAllNs()[0]));
			System.out.println("\n");
			System.out.println(xplayer.toString()+" does: "+p.getKey().toString());
			System.out.println("\n");

			if(xPlaying) {
				playTime = minTime;
			} else {
				playTime = maxTime;
			}

			p = mastUtils.MCTS(treeO, sm, oplayer, maxIter, System.currentTimeMillis() + playTime, 100);
			treeO = p.getValue();
			theJM.add(p.getKey());
			System.out.println("Legal moves: "+Arrays.toString(treeO.getLegalMoves()[1]));
			System.out.println("Q scores: "+Arrays.toString(treeO.getAllQScores()[1]));
			System.out.println("N scores: "+Arrays.toString(treeO.getAllNs()[1]));
			System.out.println("\n");
			System.out.println(oplayer.toString()+" does: "+p.getKey().toString());
			System.out.println("\n");

			xPlaying = !xPlaying;

			sm.getNextState(treeX.getState(), theJM);
			if(!sm.isTerminal(treeX.getState())) {
				treeX = treeX.getChild(theJM);
				treeX.setParent(null);
				treeO = treeO.getChild(theJM);
				treeO.setParent(null);
			}
		}

		List<Integer> g = sm.getGoals(treeX.getState());
		System.out.println(g.toString());

	}
}
