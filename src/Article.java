/**
 * Classe Article
 * @version 1.1
 * */

public class Article {

	/** 
	 * référence de l'article
	 */
	private int reference;		
	/**
	 * désignation
	 */
	private String designation;	
	/**
	 * prix unitaire hors taxe
	 */
	private double puHt;		
	/**
	 * quantité en stock
	 */
	private int qteStock;
	/**
	 * utilisateur qui a créé l'article
	 */
	private int createdBy;

	/**
	 * Constructeur
	 * @param reference référence de l'article
	 * @param designation désignation
	 * @param puHt prix unitaire hors taxe
	 * @param qteStock quantité en stock
	 */
	public Article(int reference, String designation, double puHt, int qteStock) {
		this.reference=reference;
		this.designation = designation;
		this.puHt = puHt;
		this.qteStock = qteStock;
	}
	/**
	 * Constructeur - la référence n'est pas fixée dans le programme
	 * @param designation désignation de l'article
	 * @param puHt prix unitaire hors taxe
	 * @param qteStock quantité en stock
	 */
	public Article(String designation, double puHt, int qteStock) {
		this.designation = designation;
		this.puHt = puHt;
		this.qteStock = qteStock;
	}
	/**
	 * Constructeur - ni la référence ni la qte en stock ne sont fixées dans le programme
	 * @param designation désignation de l'article
	 * @param puHt prix unitaire hors taxe
	 */
	public Article(String designation, double puHt) {
		this.designation = designation;
		this.puHt = puHt;
		this.qteStock = 0;
	}
	/**
	 * getter pour l'attribut reference
	 * @return valeur de la reference article
	 */
	public int getReference() {
		return reference;
	}
	/**
	 * setter pour l'attribut reference
	 * @param reference : nouvelle valeur de la reference article
	 */
	public void setReference(int reference) {
		this.reference = reference;
	}
	/**
	 * getter pour l'attribut désignation
	 * @return valeur de la désignation article
	 */
	public String getDesignation() {
		return designation;
	}
	/**
	 * setter  pour l'attribut designation
	 * @param designation : nouvelle valeur de la désignation article
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	/**
	 * getter  pour l'attribut puHt
	 * @return valeur de prix unitaire HT
	 */
	public double getPuHt() {
		return puHt;
	}
	/**
	 * setter  pour l'attribut puHt
	 * @param puHt :  nouvelle valeur de prix unitaire HT
	 */
	public void setPuHt(double puHt) {
		this.puHt = puHt;
	}
	/**
	 * getter  pour l'attribut qteStock
	 * @return valeur de quantité en stock
	 */
	public int getQteStock() {
		return qteStock;
	}
	/**
	 * setter  pour l'attribut qteStock
	 * @param qteStock : nouvelle valeur de prix unitaire HT
	 */
	public void setQteStock(int qteStock) {
		this.qteStock = qteStock;
	}
	/**
	 * getter pour l'attribut createdBy
	 * @return valeur de l'utilisateur qui a créé l'article
	 */
	public int getCreatedBy() {
		return createdBy;
	}
	/**
	 * setter pour l'attribut createdBy
	 * @param createdBy : nouvelle valeur de l'utilisateur qui a créé l'article
	 */
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Redéfinition de la méthode toString permettant de définir la traduction de l'objet en String
	 * pour l'affichage par exemple
	 */
	@Override
	public String toString() {
		return "Article [reference=" + reference + ", designation=" + designation + 
               ", puHt=" + puHt + ", qteStock=" + qteStock + "]";
	}
}
