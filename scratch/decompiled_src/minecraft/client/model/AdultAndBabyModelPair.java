package net.minecraft.client.model;

public record AdultAndBabyModelPair(Model adultModel, Model babyModel) {
   public Model getModel(final boolean isBaby) {
      return isBaby ? this.babyModel : this.adultModel;
   }
}
