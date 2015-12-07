package helper;

public class VoteManager
{
	//==================================================================//
	//====================== WHAT TO KNOW ==============================//
	//==================================================================//
	//= Basically just call the following classes when you receive a   =//
	//= vote from the RM:                                              =//
	//=     flightRMvote(boolean vote)                                 =//
	//=     carRMvote(boolean vote)                                    =//
	//=     roomRMvote(boolean vote)                                   =//
	//= And when the outcome is known it will signal the voteListener. =//
	//= You should call this when a socket crashes a well because      =//
	//= crashing is equilvalent to NO.                                 =//
	//=                                                                =//
	//= The voting ressets by itself, but you can always call the      =//
	//= reset() funtion if you want.                                   =//
	//==================================================================//
	
	
	
	private boolean flight = false;
	private boolean car = false;
	private boolean room = false;
	
	private VoteListener voteListener = null;
	
	public VoteManager(VoteListener listener)
	{
		voteListener = listener;
	}
	
	public void flightRMvote(boolean vote)
	{
		if(!vote)
		{
			reset();
			voteListener.voteResult(false);
		}
		else
		{
			flight = true;
			if(allYes())
			{
				reset();
				voteListener.voteResult(true);
			}
		}
	}
	
	public void carRMvote(boolean vote)
	{
		if(!vote)
		{
			reset();
			voteListener.voteResult(false);
		}
		else
		{
			car = true;
			if(allYes())
			{
				reset();
				voteListener.voteResult(true);
			}
		}
	}
	
	public void roomRMvote(boolean vote)
	{
		if(!vote)
		{
			reset();
			voteListener.voteResult(false);
		}
		else
		{
			room = true;
			if(allYes())
			{
				reset();
				voteListener.voteResult(true);
			}
		}
	}
	
	public void reset()
	{
		flight = false;
		car = false;
		room = false;
	}
	
	private boolean allYes()
	{
		return (flight && car && room);
	}
}
