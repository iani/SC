Titre {
	
	*new {
		arg fenetre, titre, long = 102, haut = 15;
		var texte;
		texte = StaticText(fenetre, Rect(0, 0, long, haut));
		// texte.background = Color.black;
		texte.stringColor_(Color.white);
		texte.string = titre;
		texte.align = \center;
	}
}
		