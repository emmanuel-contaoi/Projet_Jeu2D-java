package com.monzombie.game.entity;

/**
 * Classe abstraite qui gère la vie, les degats et l etat courant.
 */
public abstract class LivingEntity extends AbstractEntity implements Damageable {

    protected final int vieMax;
    protected int vie;
    protected EtatVie etatVie = EtatVie.VIVANT;

    /**
     * Initialise une entite vivante avec ses dimensions et points de vie.
     */
    protected LivingEntity(float width, float height, int vieMax) {
        super(width, height);
        this.vieMax = Math.max(1, vieMax);
        this.vie = this.vieMax;
    }

    @Override
    public void subirDegats(int quantite) {
        if (quantite <= 0) return;
        if (etatVie == EtatVie.MORT) return;
        reglerVie(vie - quantite);
    }

    /**
     * Modifie immédiatement le total de vie avec clamp.
     */
    public void reglerVie(int nouvelleValeur) {
        int clamp = Math.max(0, Math.min(vieMax, nouvelleValeur));
        vie = clamp;
        if (vie <= 0) {
            mourir();
        } else if (etatVie == EtatVie.MORT) {
            etatVie = EtatVie.VIVANT;
        }
    }

    /**
     * Refill toute la vie et remet l etat vivant.
     */
    public void remplirVie() {
        vie = vieMax;
        etatVie = EtatVie.VIVANT;
    }

    /**
     * Tue l entite sur le champ.
     */
    public void tuerDirect() {
        vie = 0;
        mourir();
    }

    /**
     * Permet aux sous classes de personnaliser la mort.
     */
    protected void mourir() {
        etatVie = EtatVie.MORT;
    }

    /**
     * Change l etat interne.
     */
    protected void changerEtat(EtatVie nouvelEtat) {
        etatVie = nouvelEtat;
    }

    public EtatVie getEtatVie() {
        return etatVie;
    }

    public boolean estVivant() {
        return etatVie == EtatVie.VIVANT;
    }

    @Override
    public boolean estMort() {
        return etatVie == EtatVie.MORT;
    }

    public int getVie() {
        return vie;
    }

    public int getVieMax() {
        return vieMax;
    }
}
