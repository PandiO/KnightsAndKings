package Traits;

import java.util.UUID;

public class NPCTarget
{
  public UUID targetID;
  public long ticksLeft;
  
  public int hashCode()
  {
    return this.targetID.hashCode();
  }
  
  public boolean equals(Object o)
  {
    return ((o instanceof NPCTarget)) && (((NPCTarget)o).targetID.equals(this.targetID));
  }
}
