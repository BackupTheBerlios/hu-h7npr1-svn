package pi;

import java.io.Serializable;

public interface Task
extends Serializable
{
  Result execute();
}