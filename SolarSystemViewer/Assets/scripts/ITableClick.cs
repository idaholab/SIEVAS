using System;
using UnityEngine.EventSystems;

public interface ITableClick
{
	void handleClick(PointerEventData evt, SessionCell cell);
}


