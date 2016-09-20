using System;
using UnityEngine;
using UnityEngine.UI;

public class ModalDialog
{
	//private Canvas canvas;
	//private GameObject panel;
	private GameObject go;

	public delegate bool HandleModalClick();

	public ModalDialog (string headerText, string displayText, string buttonText, HandleModalClick clickFunc)
	{
		go = UnityEngine.Object.Instantiate (Resources.Load ("modal_prefab")) as GameObject;

		Transform headerTransform = go.transform.Find ("Panel/lblModalHeader");
		headerTransform.gameObject.GetComponent<Text> ().text = headerText;
		Transform textTransform = go.transform.Find ("Panel/lblModalText");
		textTransform.gameObject.GetComponent<Text> ().text = displayText;
		Transform btnTextTransform = go.transform.Find ("Panel/btnOK/Text");
		btnTextTransform.GetComponent<Text> ().text = buttonText;

		Transform btnTransform = go.transform.Find ("Panel/btnOK");
		if (clickFunc != null)
			btnTransform.gameObject.GetComponent<Button> ().onClick.AddListener (() => {handleClick (); clickFunc ();});
		else
			btnTransform.gameObject.GetComponent<Button> ().onClick.AddListener (() => handleClick ());
	}

	private void handleClick()
	{
		UnityEngine.Object.Destroy (go);
	}

	public void show()
	{
		go.SetActive (true);
	}

	public void hide()
	{
		go.SetActive (false);
	}


}

