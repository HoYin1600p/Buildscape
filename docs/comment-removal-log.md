# Comment Removal Log

This audit removes comments from Java source, tests, and Gradle build logic. String literals and Java text blocks were parsed as source content and retained unchanged.

- Files scanned: 511
- Files changed: 254
- Comments removed: 4284

## Per-file Summary

| File | Comments removed |
| --- | ---: |
| `build.gradle` | 5 |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 55 |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 14 |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiCache.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 20 |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateRequest.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateResponse.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/api/model/MembershipTier.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/api/model/RedeemCodeRequest.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/api/model/SelectCosmeticRequest.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/api/model/SupporterStatus.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/api/model/TiersResponse.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 35 |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlock.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlock.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlockEntity.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/block/CascadeWaterManager.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 30 |
| `src/main/java/com/kingodogo/buildscape/block/CreakingHeartBlock.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/block/ExperienceFluidBlock.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/block/EyeblossomBlock.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/block/FireflyBushBlock.java` | 6 |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 11 |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 19 |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/block/GrassSlabBlock.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 22 |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 32 |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 107 |
| `src/main/java/com/kingodogo/buildscape/block/ModSlabBlock.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java` | 6 |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 26 |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 113 |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 11 |
| `src/main/java/com/kingodogo/buildscape/block/PotentSulfurBlockEntity.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/block/ShelfBlock.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyGlassBlock.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyPaneBlock.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 13 |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlockEntity.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/block/SoftFabricBlock.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/block/SpoolBlock.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/block/StrawBedBlock.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/block/SulfurSpikeBlock.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 25 |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 14 |
| `src/main/java/com/kingodogo/buildscape/client/BuildscapeRenderLayers.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/ClientAdvancementEvents.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/client/CopperChestClientEvents.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/GeyserParticleHandler.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 16 |
| `src/main/java/com/kingodogo/buildscape/client/ModKeyBinds.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 7 |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 11 |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 19 |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 13 |
| `src/main/java/com/kingodogo/buildscape/client/WrenchClientHandler.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/client/event/ClientFluidEvents.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 15 |
| `src/main/java/com/kingodogo/buildscape/client/performance/BuildscapeBlockStateCacheCoordinator.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/performance/BuildscapeStartupWork.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/performance/LaunchFasterInterop.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 28 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 35 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/DecoratedPotBlockEntityRenderer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/EmbeddiumSpillBuffer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/FallingIcicleRenderer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/FestiveStockingBlockEntityRenderer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/FestiveStockingRenderer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 8 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 43 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 7 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MangroveBoatRenderer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 169 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobState.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 56 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 85 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeWaterSurface.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ShelfRenderer.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/SignFrameRenderer.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/TrappedDecoratedPotBlockEntityRenderer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 52 |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersPouchScreen.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 29 |
| `src/main/java/com/kingodogo/buildscape/client/screen/DebugRenderConfig.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 13 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 62 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 324 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 135 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 469 |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 13 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 8 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 34 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 24 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/FlatIconButton.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 17 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 41 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 66 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 21 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 36 |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 26 |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 14 |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 14 |
| `src/main/java/com/kingodogo/buildscape/client/workbench/ClientBlockColorCatalog.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/config/BuildscapeClientConfig.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 16 |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 17 |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 14 |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 189 |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 46 |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 18 |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 33 |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameAttachment.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameInteractionHandler.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameType.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosArmor.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosChest.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosFeet.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosHead.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosLegs.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 57 |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 40 |
| `src/main/java/com/kingodogo/buildscape/data/ModBlockTagsProvider.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/data/ModDataGen.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/data/ModRecipeProvider.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 19 |
| `src/main/java/com/kingodogo/buildscape/entity/ModEntities.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 7 |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 22 |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 8 |
| `src/main/java/com/kingodogo/buildscape/event/FestiveGlintAnvilHandler.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/event/FrostRoseDropHandler.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 11 |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 47 |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 7 |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 7 |
| `src/main/java/com/kingodogo/buildscape/event/StrawBedHandler.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/event/TagTooltipHandler.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/event/WanderingHomemakerSpawningHandler.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/firework/CustomFireworkRenderer.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 23 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 49 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 29 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 49 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 16 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 21 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 26 |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 18 |
| `src/main/java/com/kingodogo/buildscape/item/BottleOfMistItem.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/item/ConfettiItem.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/item/ExperienceBucketItem.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 112 |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 62 |
| `src/main/java/com/kingodogo/buildscape/item/StringlightFrameItem.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 8 |
| `src/main/java/com/kingodogo/buildscape/mixin/BaseCoralPlantTypeBlockMixin.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/mixin/BeaconBlockEntityMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeBlockModelMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeBlockStateCacheMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeForgeRegistryMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeMixinPlugin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeModelBakeryMixin.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/mixin/ComposterBlockMixin.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/mixin/CreativeModeTabMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/EmbeddiumPipeSpillMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/EntityMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/FeatureMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/FireworkStarterMixin.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/mixin/FlowingFluidMixin.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/mixin/ItemMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/LeavesBlockMixin.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/mixin/LiquidBlockRendererMixin.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/mixin/PauseScreenMixin.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/mixin/RenderBuffersMixin.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 10 |
| `src/main/java/com/kingodogo/buildscape/mixin/StonecutterScreenMixin.java` | 6 |
| `src/main/java/com/kingodogo/buildscape/network/BuildersPouchMenu.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 26 |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchResultsPacket.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/network/RemovePillarPacket.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 7 |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 7 |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 8 |
| `src/main/java/com/kingodogo/buildscape/network/SyncSignFramePacket.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/network/UpdateAllPillarIdsPacket.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 22 |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/particle/CakeParticle.java` | 6 |
| `src/main/java/com/kingodogo/buildscape/particle/CascadeParticle.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 14 |
| `src/main/java/com/kingodogo/buildscape/particle/ColoredSmokeParticle.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 29 |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 12 |
| `src/main/java/com/kingodogo/buildscape/particle/GeyserPlumeParticle.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/particle/NoxiousGasParticle.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/particle/PillarSparkleParticle.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/particle/SmokeColorRegistry.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeParticle.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeStillParticle.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 14 |
| `src/main/java/com/kingodogo/buildscape/particle/TrailNoteParticle.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/particle/XpParticle.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 30 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnState.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/HollowPipeTransportManager.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFlowState.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 8 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeItemTransit.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeOutletWater.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 8 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 49 |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WorldPipeTopologyAccess.java` | 4 |
| `src/main/java/com/kingodogo/buildscape/recipe/CustomFireworkStarRecipe.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/recipe/ShapedDurabilityRecipe.java` | 2 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java` | 6 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 30 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/AliasResolver.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/FamilyExpander.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/TemplateEngine.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/integration/RecipeManagerInjector.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/parser/RecipeIR.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/parser/StreamingRecipeParser.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/util/IngredientCache.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/validation/RecipeValidator.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 15 |
| `src/main/java/com/kingodogo/buildscape/stat/ModStats.java` | 1 |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 9 |
| `src/main/java/com/kingodogo/buildscape/util/ColorGradientSolver.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/util/FestiveGlintHelper.java` | 3 |
| `src/main/java/com/kingodogo/buildscape/world/ModGameRules.java` | 5 |
| `src/main/java/com/kingodogo/buildscape/worldgen/CreakingHeartTreeDecorator.java` | 1 |
| `src/test/java/com/kingodogo/buildscape/client/renderer/PipeSpillTest.java` | 3 |
| `src/test/java/com/kingodogo/buildscape/pipe/transport/PipeOutletWaterTest.java` | 1 |

## Removed Comments

| File | Original line(s) | Type | Content preview |
| --- | --- | --- | --- |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 9-13 | block | * * Manages one-time authentication at game launch for cosmetic data. * Authentication happens once when the game starts, and the data is cached * for the entire game session. |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 29-34 | block | * * Authenticate once at game launch and cache the cosmetic data. * This method is thread-safe and ensures authentication only happens once. * * @return CompletableFuture with CosmeticData, or null if authentication fails |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 36 | line | Return cached data if already authenticated |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 42 | line | Return existing future if authentication is already in progress |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 47 | line | Double-check after acquiring lock |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 52 | line | Get UUID and access token from Minecraft |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 72 | line | Call the secure API and share the future |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 87 | line | Convert response to CosmeticData |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 90 | line | Cache the result |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 111-113 | block | * * Check if authentication has been completed successfully. |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 118-121 | block | * * Get the cached cosmetic data. * Returns null if authentication hasn't completed yet. |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 126-128 | block | * * Get the timestamp of successful authentication. |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 133-136 | block | * * Clear the authentication cache. * This should only be called when the game is shutting down. |
| `src/main/java/com/kingodogo/buildscape/api/CosmeticAuthManager.java` | 146-149 | block | * * Force re-authentication on next call to authenticateOnLaunch(). * Use with caution - this will trigger a new API call. |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateRequest.java` | 3-6 | block | * * Request body for the secure authenticate endpoint. * POST /api/minecraft |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateRequest.java` | 21-23 | block | * * Create a standard authenticate request. |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateResponse.java` | 6-9 | block | * * Response from the secure authenticate endpoint. * POST /api/minecraft |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateResponse.java` | 16 | line | Error fields (when authentication fails) |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateResponse.java` | 67-69 | block | * * Check if the response indicates an error. |
| `src/main/java/com/kingodogo/buildscape/api/model/AuthenticateResponse.java` | 78-80 | block | * * Convert this response to CosmeticData format. |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 6-16 | block | * * Response model for cosmetics API endpoint. * Supports both legacy format (GET /api/v1/supporters/cosmetics/{uuid}) * and secure format (POST /api/minecraft with authentication). * * Arrays contain cosmetic IDs in format: * - "item:names... |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 18 | line | Legacy fields (backward compatibility) |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 23 | line | New secure API fields |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 38 | line | Legacy getters/setters |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 63 | line | New secure API getters/setters |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 96-99 | block | * * Adapter method to populate legacy fields from new secure API response. * Call this after receiving data from the new API. |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 101 | line | Populate unlocked from unlockedCosmetics (combines defaults + unlocked) |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 107 | line | If admin, we can't easily list everything here without CosmeticManager |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 108 | line | But we can flag it for the UI |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 111 | line | Populate equipped from selectedCosmetics values |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 119 | line | locked is not provided by new API, leave as null/empty |
| `src/main/java/com/kingodogo/buildscape/api/model/CosmeticData.java` | 123-125 | block | * * Check if this data came from the secure API (new format). |
| `src/main/java/com/kingodogo/buildscape/api/model/MembershipTier.java` | 3-6 | block | * * Model for membership tier information. * Part of GET /api/v1/supporters/tiers response. |
| `src/main/java/com/kingodogo/buildscape/api/model/RedeemCodeRequest.java` | 3-6 | block | * * Request body for the secure redeem code endpoint. * POST /api/redeem |
| `src/main/java/com/kingodogo/buildscape/api/model/RedeemCodeRequest.java` | 23-25 | block | * * Create a standard redeem code request. |
| `src/main/java/com/kingodogo/buildscape/api/model/SelectCosmeticRequest.java` | 3-6 | block | * * Request body for the secure select cosmetic endpoint. * POST /api/cosmetics |
| `src/main/java/com/kingodogo/buildscape/api/model/SupporterStatus.java` | 5-8 | block | * * Response model for supporter status API endpoint. * GET /api/v1/supporters/status/{uuid} |
| `src/main/java/com/kingodogo/buildscape/api/model/TiersResponse.java` | 5-8 | block | * * Response model for tiers API endpoint. * GET /api/v1/supporters/tiers |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiCache.java` | 11 | line | Session-lifetime cache: cosmetics should NEVER expire during a game session. |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiCache.java` | 12 | line | They are only invalidated explicitly on logout/disconnect. |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 67 | line | Suppressed debug log to prevent render loop spam |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 221-228 | block | * * Authenticate with the secure API using Minecraft session credentials. * This is the new secure authentication method that verifies the access token with Mojang. * * @param uuid The player's UUID * @param accessToken The player's Minecra... |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 238 | line | Sanitize inputs |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 242 | line | Create request body |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 245 | line | Make POST request to secure API |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 281 | line | Check for HTTP error status |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 285 | line | Error details should be in the response body |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 307-315 | block | * * Redeem a code for the player. * This uses the secure API and requires authentication appropriately. * * @param uuid The player's UUID * @param accessToken The player's Minecraft access token for verification * @param code The code to re... |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 329 | line | Sanitize inputs |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 332 | line | Simple alphanumeric check for code |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 335 | line | Create request body |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 338 | line | Make POST request to secure API |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 375 | line | Check for HTTP error status |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 376 | line | Even if status is 4xx, the body might contain useful error message |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 382 | line | Ensure success is false if http error |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 406-408 | block | * * Select a cosmetic for a player. |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 426 | line | Convert string UUID to UUID object and check rate limit |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 427 | line | Handle both formats: with dashes and without dashes |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 432 | line | Insert dashes to make valid UUID format |
| `src/main/java/com/kingodogo/buildscape/api/SupportersApiClient.java` | 455 | line | Use the secure API |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 27 | line | Cache shapes: [Facing Index (0:N, 1:E, 2:S, 3:W)][Part Index (0:SINGLE, 1:BOTTOM, 2:MIDDLE, 3:TOP)] |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 43 | line | 1. Main Central Log body (always present) |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 44 | line | Model: from [0,0,3] to [18,5.5,13] — X clamped to 16 (anchor side extends beyond block) |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 47 | line | 2. Seat / cushion layers (always present) — 3 individual layers from the model, no rotation |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 48 | line | Model: bottom padding [4,5,4.75]→[12,6.5,11.25] |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 50 | line | Model: middle ledge [5,6.5,5.75]→[11,7.5,10.25] |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 52 | line | Model: top rail [4,7.5,4.75]→[12,8.5,11.25] |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 55 | line | 3. Handle side — only on SINGLE or BOTTOM |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 56 | line | Front-face tooth prongs (model elements, no rotation): 3 small studs at z~3 |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 63 | line | 4. Anchor side — only on SINGLE or TOP |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 64 | line | Back stud (model element, no rotation): 1 stud at z~13 |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 69 | line | NOTE: The tilted handle column (-22.5° z) and anchor column (+22.5° z) are intentionally |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 70 | line | excluded — axis-aligned VoxelShapes cannot represent rotated geometry without creating |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 71 | line | oversized bounding boxes that visually spill far outside the actual model. |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 81 | line | NORTH |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 111 | line | Don't interact with neighbors for item transfer - AshenKingPillars are independent |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 112 | line | Only return SINGLE state, never connect to stacks |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 117 | line | Ashen King Pillars always render as SINGLE - no connection to neighbors |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 160-163 | block | * * Override to prevent item transfer behavior (Ashen King Pillars don't connect/stack) * Each pillar is independent - no item stacking enforcement |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 166 | line | AshenKingPillar overrides onPlace to skip the enforceSingleItemPerStack() call from parent |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 167 | line | Each pillar keeps its own item when stacked |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 169 | line | Don't call parent's onPlace which enforces single item transfer |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 170 | line | Just notify neighbors of the block state change |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 182-185 | block | * * Override to prevent any connection or item transfer behavior * Ashen King Pillars always render as SINGLE and do NOT transfer items |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 188 | line | Ashen King Pillars never connect to neighbors, always remain SINGLE |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 189 | line | Don't call parent's neighborChanged which enforces item transfer |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 192-195 | block | * * Override to prevent item interaction with stacked pillars * Each AshenKingPillar is independent - no cross-pillar item logic |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 216 | line | Check for dye items |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 281 | line | Handle shift-click for particle pattern cycling |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 318 | line | Handle item removal - ONLY from current pillar, not from stack |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 338 | line | Handle item placement - ONLY on current pillar |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 358 | line | Intentionally removed: pillarBE.setParticlePattern(cfg.pattern); |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 374-376 | block | * * Get dye color and name from ItemStack |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 417-419 | block | * * Get direction name from yaw |
| `src/main/java/com/kingodogo/buildscape/block/AshenKingPillarBlock.java` | 430-432 | block | * * Get pattern color |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlock.java` | 36 | line | ── Block Entity ────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlock.java` | 56 | line | ── Interaction ─────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlock.java` | 70 | line | ── Drop inventory on break ─────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 26-34 | block | * * Slot layout (30 total workbench slots): * 0 – Color Picker tool slot * 1–9 – Color result slots * 10 – Input Pouch slot * 11 – Output Pouch slot * 12–20 – Gradient Output slots (server-written, read-only for players) * 21–29 – Gradient ... |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 44 | line | 0–29 |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 51 | line | Persisted filter/tab state |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 52 | line | 0=ColorPicker, 1=GradientBuilder |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 53-56 | block | * * Bumped when the meaning of the saved mask changes; older tags fall back to * FILTER_DEFAULT instead of silently keeping the previous all-on state. |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 110 | line | ── MenuProvider ────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 123 | line | ── Container ───────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 231 | line | 2 seconds |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 312 | line | Find the first row (0, 1, or 2) that can fit the non-empty solved filters |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 330 | line | Ideal: completely empty row! |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 382 | line | ── Accessors ───────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/BuildersWorkbenchBlockEntity.java` | 530 | line | ── NBT ─────────────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlock.java` | 106 | line | Glass bottle on cascade block -> collect mist, replace with no-mist variant |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlock.java` | 109 | line | Preserve waterlogged property when replacing the block |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlock.java` | 127 | line | Empty hand -> tune cascade particle density (20%, 40%, 60%, 80%, 100%) |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlockEntity.java` | 21 | line | 1 = 20%, 2 = 40%, 3 = 60%, 4 = 80%, 5 = 100% |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlockEntity.java` | 100 | line | Respect Minecraft's particle settings for performance |
| `src/main/java/com/kingodogo/buildscape/block/CascadeBlockEntity.java` | 109 | line | If local player holds cascade block or bottle of mist in OFF-HAND, suppress particles in their chunk |
| `src/main/java/com/kingodogo/buildscape/block/CascadeWaterManager.java` | 40 | line | If ticket already exists, invalidate it before creating a new one to be safe |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 87 | line | Can bonemeal if there is air or replaceable block above, or if any replaceable block is nearby |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 103 | line | vertical spread of -1 to 1 |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 114 | line | Change the block to this colored moss block |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 118 | line | Try to spawn carpet/overlay/layers/sapling/flower on top |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 124 | line | 25% chance to place carpet |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 129 | line | 10% chance to place overlay |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 134 | line | 10% chance to place layers (1 layer) |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 139 | line | 13% chance to place sapling |
| `src/main/java/com/kingodogo/buildscape/block/ColoredMossBlock.java` | 146 | line | 12% chance to place flower (e.g. eyeblossom) |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 40 | line | 1. Chiseled Copper |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 47 | line | 2. Copper Grate |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 54 | line | 3. Copper Bulb |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 61 | line | 4. Copper Rod |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 68 | line | 5. Copper Lantern |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 75 | line | 6. Copper Door |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 82 | line | 7. Copper Trapdoor |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 89 | line | 8. Copper Bars |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 96 | line | Copper Meshes |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 103 | line | Copper Bolts |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 110 | line | 9. Cut Copper Vertical Slab |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 117 | line | 10. Copper Button |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 124 | line | 11. Copper Pressure Plate |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 131 | line | 12. Copper Chests |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 138 | line | 13. Copper Chains |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 141 | line | 14. Large Copper Chains |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 144 | line | 15. Slit Copper (Block, Stairs, Slab, Vertical Slab) |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 194 | line | 0. BOTTLE OF MIST OXIDATION SPEEDUP |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 227 | line | 1. HONEYCOMB WAXING |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 233 | line | Wax on particles |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 249 | line | Wax on particles |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 262 | line | 2. AXE INTERACTION (UNWAXING & DE-OXIDIZING / SCRAPING) |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 264 | line | Check Eyeblossom unwaxing |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 269 | line | Wax off particles |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 280 | line | A) Check Unwaxing |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 286 | line | Wax off particles |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 298 | line | B) Check De-oxidizing / Scraping |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 304 | line | Scrape particles |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 328 | line | 1 in 16 chance on random tick to progress oxidation |
| `src/main/java/com/kingodogo/buildscape/block/CopperOxidationHandler.java` | 376 | line | Suppress intermediate shape updates so neither half is detached while the pair changes. |
| `src/main/java/com/kingodogo/buildscape/block/CreakingHeartBlock.java` | 46 | line | Set blockstate to ACTIVE = true |
| `src/main/java/com/kingodogo/buildscape/block/CreakingHeartBlock.java` | 49 | line | Spawn resin clumps directly onto sides of Creaking Heart block |
| `src/main/java/com/kingodogo/buildscape/block/CreakingHeartBlock.java` | 52 | line | Play sound and particles |
| `src/main/java/com/kingodogo/buildscape/block/CreakingHeartBlock.java` | 56 | line | Schedule tick to deactivate after 2 seconds (40 ticks) |
| `src/main/java/com/kingodogo/buildscape/block/ExperienceFluidBlock.java` | 21 | line | Spawn popping particles similar to lava but using XP particle |
| `src/main/java/com/kingodogo/buildscape/block/EyeblossomBlock.java` | 57 | line | Close flower during day |
| `src/main/java/com/kingodogo/buildscape/block/EyeblossomBlock.java` | 61 | line | Open flower during night |
| `src/main/java/com/kingodogo/buildscape/block/FireflyBushBlock.java` | 44 | line | Spawn firefly particle when placed, but only if it's night or there's no skylight |
| `src/main/java/com/kingodogo/buildscape/block/FireflyBushBlock.java` | 66 | line | Player proximity optimization for server performance |
| `src/main/java/com/kingodogo/buildscape/block/FireflyBushBlock.java` | 68 | line | Check again in 1 second |
| `src/main/java/com/kingodogo/buildscape/block/FireflyBushBlock.java` | 72 | line | On each tick, check if it's night or there's no skylight |
| `src/main/java/com/kingodogo/buildscape/block/FireflyBushBlock.java` | 76 | line | a firefly bush has a 70% chance of emitting a new firefly particle |
| `src/main/java/com/kingodogo/buildscape/block/FireflyBushBlock.java` | 78 | line | in an air block up to 5 blocks above the bush and horizontally up to 5 blocks away |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 340 | line | Check for 5 Frost Roses in a 7x7x7 block radius (so offset by 3) |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 350 | line | Target "perfect" rate: A 7x7 field (49 roses) with a 1/15 chance. |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 351 | line | This calculates the exact relative probability so ANY size cluster |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 352 | line | generates the exact same total amount of snow per second. |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 356 | line | Mostly 15-20 particles, but sometimes randomly higher up to 50 |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 357 | line | 15 to 20 |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 358 | line | 20% chance to be higher |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 359 | line | 21 to 50 |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 362 | line | Spawn snowfall over an 80 block diameter (40 block radius) |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 365 | line | Uniformly distribute in the circle |
| `src/main/java/com/kingodogo/buildscape/block/FrostRoseBlock.java` | 372 | line | Spawn above the area so it falls down |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 183 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 184 | line | 1. INSERTION (Normal Right Click holding food or liquid item) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 185 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 227 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 228 | line | 2. EXTRACTION TO HAND (Sneak + Right Click) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 229 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 231 | line | A. Extract Food (Empty hand OR holding same stored food item) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 259 | line | B. Extract Potion/Honey bottle (Empty hand) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 274 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 275 | line | 2.5. EXTRACTION WITH BUCKET/BOTTLE (Does not require sneaking) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 276 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 277 | line | C. Empty Bucket extraction (XP: requires XP_BOTTLE_MAX; others: requires full 16 levels) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 301 | line | D. Empty Bottle extraction (Requires liquid level > 0, excluding Lava) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 320 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 321 | line | 3. NORMAL RIGHT CLICK WITH EMPTY HAND (Direct Consumption/Drinking from jar) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 322 | line | ------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 324 | line | A. Consume food from jar if player can eat |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 343 | line | B. Drink potion / liquid directly from jar |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlock.java` | 347 | line | Do not allow consuming lava or XP liquid from jars |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 22 | block | * Standard max liquid level (1 bucket or 16 potion/honey bottles). |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 24 | block | * XP bottles needed to fill the jar (3 bottles = full). |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 29 | line | 0 to MAX_LIQUID_LEVEL (or XP_BOTTLE_MAX for XP liquid) |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 137 | line | Reject mob/fish buckets |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 142 | line | Accept standard buckets |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 147 | line | Reject splash & lingering potions |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 152 | line | Accept experience bottle |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 157 | line | Accept normal potions & honey bottles |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 191 | line | XP liquid fills up after XP_BOTTLE_MAX bottles; all others cap at MAX_LIQUID_LEVEL |
| `src/main/java/com/kingodogo/buildscape/block/GlassJarBlockEntity.java` | 270 | line | XP liquid only needs XP_BOTTLE_MAX level to extract a bucket; others need MAX_LIQUID_LEVEL |
| `src/main/java/com/kingodogo/buildscape/block/GrassSlabBlock.java` | 117 | line | Decay to Dirt Slab |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 205 | line | If holding empty flower pot, allowed only if log has no decoration and pot is allowed |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 208 | line | If log already has decoration, plant/flower can be inserted into an existing flower pot |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 213 | line | Empty log: only full blocks are valid interior decoration |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 246 | line | 1. Placing Glass Cover — ONLY IF FLUID IS PRESENT |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 270 | line | 2. Fluid interactions (Lava, Water, Experience, or Modded Fluids) — REJECT IF DECORATION IS PRESENT |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 279 | line | Empty Bucket interaction to retrieve fluid (SOURCE ONLY) |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 307 | line | Filled Fluid Bucket interaction to deposit fluid |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 311 | line | NEVER allow replacing an existing fluid in a log! Only one fluid type per blockspace. |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 353 | line | 3. Inserting Flower/Plant/Foliage into an existing Empty Flower Pot inside Hollow Log |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 369 | line | 4. Hollow Log Interior Decoration — REJECT IF FLUID IS PRESENT |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 370 | line | Foliage / flowers / plants can ONLY be placed if an empty flower pot is already placed inside the log (Section 3). |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 374 | line | A. Placing an empty Flower Pot |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 391 | line | B. Placing a Full Block Decoration |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 415 | line | 5. Removing glass cover or decoration with empty hand or sneaking |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 492 | line | Standard Minecraft Glass Classes |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 497 | line | Check Tags (forge:glass/colorless, forge:glass, etc.) |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 502 | line | Buildscape & Modded Glass Blocks: check registry name for "glass" |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 520 | line | Reject non-full-cube / partial / utility blocks (including Fences, Gates, Panes, Walls, Slabs, Stairs, Chains, etc.) |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 536 | line | Reject ALL glass blocks and glass items from being placed as interior block decorations |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 548 | line | Shape check: must be a full 1x1x1 cube block |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 702 | line | Sync BlockState properties with NBT contents from copied BlockEntityTag |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlock.java` | 760 | line | Do NOT modify or infect other hollow logs or hollow pipes |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 63-66 | block | * * Sets a target flow state that will become active after delayTicks server ticks. * Used for natural, block-by-block animated water flow and drainage progression. |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 69 | line | Neighbor updates can request the same transition repeatedly while a stream is |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 70 | line | settling. Do not restart its countdown or endpoint replacement can remain stale. |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 178 | line | --- Animated block-by-block flow progression --- |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 189 | line | --- Lava tick burn logic --- |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 194 | line | Burn away log when timer expires! |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 201 | line | Drop glass cover items if present |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 212 | line | Replace with lava or fire |
| `src/main/java/com/kingodogo/buildscape/block/HollowLogBlockEntity.java` | 276 | line | The outlet spill lives in the adjacent water's chunk mesh, not the block entity renderer. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 66-70 | block | * * Highest water surface that fits beneath the pipe's two-pixel ceiling. * This is the source level for the internal channel; flowing levels below * it use vanilla's normal 7/9 through 1/9 sequence. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 72-76 | block | * * Set by the BFS transport manager to represent the flowing water level inside this pipe. * This is internal channel state for the block-entity renderer and transport logic, not a * vanilla FluidState for the whole block volume. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 114 | line | Fast bitmask lookup table for selection/outline shapes (0..63) |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 252 | line | A set directional property denotes a seamless connection to another |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 253 | line | pipe, never an opening into the world. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 260 | line | A pipe with one attached segment has one exposed end along its primary |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 261 | line | axis. Junctions have no implicit world-facing endpoint. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 273 | line | 1. Wrench configuration: sneak-click rotates axis, normal click toggles open side (min 2 openings) |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 293 | line | Attempting to close this face: ensure at least 2 open faces remain |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 318 | line | 2. Empty Bucket interaction to retrieve fluid (SOURCE ONLY) |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 349 | line | 3. Filled Fluid Bucket interaction to deposit fluid |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 390 | line | The transport manager chooses the downstream endpoint. Do not |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 391 | line | spread from every physical opening here, because the inlet side |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 392 | line | must remain an inlet rather than creating water beside the pipe. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 398 | line | 4. Modded Forge Fluid Handler interaction (Tanks, Universal Buckets) |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 434-441 | block | * * Spreads fluid to the world from all open endpoints of this pipe. * * @param dist The BFS horizontal distance of this pipe from the source. Used to calculate * the outflow flow level so the water continues at the correct vanilla level * ... |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 446-450 | block | * * Spreads only through transport-approved exit faces. A null set preserves * the legacy behaviour for non-pipe callers; steel-pipe transport always * supplies its downstream directions. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 454 | line | 7-dist |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 459 | line | Downward exits (waterfalls) always use full strength because the BFS |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 460 | line | resets the distance counter on a vertical drop, just like vanilla does. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 464 | line | pipe has used up all 7 horizontal blocks |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 470-476 | block | * * Places a flowing fluid block at a single neighbor position. * * @param amount The flow amount (1–7). Amount 7 = strongest flow (adjacent to source), * amount 1 = weakest flow. This continues the vanilla flow chain rather * than restarti... |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 496 | line | Always reschedule so vanilla fluid tick keeps the block alive and spreads it further |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 522 | line | Refresh only the endpoint selected by the directed transport |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 523 | line | graph. This prevents water from escaping through side walls |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 524 | line | or back through the inlet. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 631 | line | Apply contained water / bubble column physics directly to players and in-world ItemEntity objects |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 737 | line | Schedule a block tick when carrying water so outflow endpoints stay refreshed |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 742 | line | Preserve WATER_LEVEL across the updateConnections recalculation |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 834 | line | Water in steel pipes is handled as channel transport through the hollow gap. |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 835 | line | Letting vanilla place water into the block would fill the full block volume |
| `src/main/java/com/kingodogo/buildscape/block/HollowPipeBlock.java` | 836 | line | and make outside water visually attach to the pipe shell. |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 18-20 | block | * * A block that acts as a slab and allows players to sit on it. |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 33 | line | Only allow sitting on right-click with empty hand (or specific conditions) |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 34 | line | If player is holding a block and not sneaking, they should place it (InteractionResult.PASS) |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 37 | line | Check if player is holding an item that might be placed |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 42 | line | Check if player is already riding something |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 47 | line | Check if there is already someone sitting here |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 53 | line | Calculate seat height based on slab type |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 54 | line | Default for bottom slab |
| `src/main/java/com/kingodogo/buildscape/block/LogSlabBlock.java` | 62 | line | Create seat entity and make player sit |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 27 | line | 1. Poplar |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 33 | line | Ashpen |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 41 | line | 2. Pale Oak |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 49 | line | 3. Cherry |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 57 | line | 4. Mangrove |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 63 | line | 5. Bamboo |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 66 | line | 6. Vanilla Log Slabs |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 74 | line | 7. Vanilla Log Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 82 | line | Wood Walls |
| `src/main/java/com/kingodogo/buildscape/block/LogStrippingHandler.java` | 95 | line | Hollow Logs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 2661 | line | ===================================================================== |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 2662 | line | Trapped Decorated Pots â€“ same textures, spawn-egg trap mechanic |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 2663 | line | ===================================================================== |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 5921 | line | Scraped Steel Slabs/Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 5932 | line | Rustic Scraped Steel Slabs/Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 5943 | line | Stacked Steel Slabs/Vertical Slabs/Stairs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 5959 | line | Steel Panels Slabs/Vertical Slabs/Stairs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 5975 | line | Crossed Steel Panels Slabs/Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 5986 | line | Steel Mesh Block Slabs/Vertical Slabs/Stairs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6001 | line | Steel Block Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6017 | line | Pressed Steel Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6033 | line | Cut Steel Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6049 | line | Polished Steel Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6168 | line | Caution Blocks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6176 | line | Caution Stairs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6365 | line | Caution Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6457 | line | Factory Glass Blocks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6570 | line | Factory Glass Panes |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6683 | line | Stained Bricks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6691 | line | Stained Bricks Stairs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 6918 | line | Stained Brick Tiles |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7031 | line | Stained Brick Tiles Slabs, Stairs, and Walls |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7393 | line | Stained Bricks Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7506 | line | Stained Bricks Walls |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7631 | line | Polished White Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7646 | line | Polished Orange Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7661 | line | Polished Magenta Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7676 | line | Polished Light Blue Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7691 | line | Polished Yellow Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7706 | line | Polished Lime Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7721 | line | Polished Pink Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7736 | line | Polished Gray Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7751 | line | Polished Light Gray Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7766 | line | Polished Cyan Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7781 | line | Polished Purple Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7796 | line | Polished Blue Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7811 | line | Polished Brown Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7826 | line | Polished Green Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7841 | line | Polished Red Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 7856 | line | Polished Black Concrete Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 8063 | line | Colored Redstone Lamps |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 9055 | line | Log Slabs with Sitting Feature |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 9146 | line | Ashpen Log |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 9184 | line | Static vertical slab and stair variants generated from BuildScape horizontal |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 9185 | line | variants. |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 9812 | line | Stained Brick Tiles Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10450 | line | End generated vertical variants. |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10452 | line | Dye Sacks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10489 | line | Spools |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10528 | line | Sulfur Blocks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10565 | line | Cinnabar Blocks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10598 | line | Poplar Wood Set Blocks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10668 | line | Wool Slabs, Stairs & Walls |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10785 | line | Red Moss variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10803 | line | Orange Moss variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10821 | line | Yellow Moss variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10839 | line | Tuff Variants |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 10871 | line | Vanilla normal slab prerequisites for vertical slabs. |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 11031 | line | Vanilla family vertical slabs. |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 11134 | line | Extended vanilla slab and stair coverage. |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 11487 | line | Vanilla gap slab and stair coverage. |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 11791 | line | Wallpaper Slabs and Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 11969 | line | Wallpaper Flat Blocks |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12019 | line | End new main vertical variants. |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12021 | line | Cushions |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12055 | line | Straw Bed |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12059 | line | Big Books |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12097 | line | --- PALE OAK WOODSET --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12131 | line | Pale Oak Log/Wood Slabs & Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12141 | line | --- CHERRY WOODSET --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12175 | line | Cherry Log/Wood Slabs & Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12185 | line | --- PALE MOSS FAMILY --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12206 | line | --- CREAKING HEART & RESIN SET --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12232 | line | --- PLANTS & FLOWERS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12256 | line | --- POTTED PLANTS & FLOWERS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12314 | line | --- FROGLIGHTS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12322 | line | --- SCULK FAMILY --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12342 | line | --- COPPER TORCH & LIGHTING --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12348 | line | --- COPPER RODS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12366 | line | --- COPPER LANTERNS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12384 | line | --- COPPER EXPANSION BLOCKS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12385 | line | Slit Copper |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12395 | line | Slit Copper Stairs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12405 | line | Slit Copper Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12415 | line | Slit Copper Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12425 | line | Chiseled Copper |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12435 | line | Copper Grates |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12445 | line | Copper Bulbs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12487 | line | Copper Doors |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12497 | line | Copper Trapdoors |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12507 | line | Copper Bars |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12517 | line | Copper Buttons |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12527 | line | Copper Pressure Plates |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12537 | line | Copper Bolts |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12547 | line | Glass Jars |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12568 | line | Snowy Poplar Leaves, Layers, and Hedges |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12659 | line | --- WOOD & BAMBOO LADDERS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12721 | line | --- COPPER CHESTS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12739 | line | --- BARS AND MESHES --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12770 | line | --- FROGLIGHTS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12798 | line | --- LARGE COPPER CHAINS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12828 | line | --- LAYERED WOOLS --- |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 12878 | line | Layered Wool Slabs, Stairs, Walls, Carpets, Layers & Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 13880 | line | CARDBOARD BLOCKS BEGIN |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 14576 | line | CARDBOARD BLOCKS END |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 14614 | line | emitting max brightness |
| `src/main/java/com/kingodogo/buildscape/block/ModBlocks.java` | 14622 | line | emitting max brightness |
| `src/main/java/com/kingodogo/buildscape/block/ModSlabBlock.java` | 32 | line | Secondary constructor for non-glass slabs using only properties |
| `src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java` | 358 | line | Tinted glass won't color the beam |
| `src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java` | 361 | line | For other ornaments, use their map color to tint the beacon beam |
| `src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java` | 395 | line | Check if a Beacon is what's actively checking the block's opacity. |
| `src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java` | 396 | line | This explicitly isolates Beacon beam checks from Grass random ticks / light engine. |
| `src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java` | 407 | line | Safe fallback |
| `src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java` | 410 | line | Natural light completely passes through perfectly |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 101 | line | Only stack with regular PillarBlocks, NOT AshenKingPillars (they're independent) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 106 | line | Only transfer items from stacked regular pillars, NOT AshenKingPillars |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 184 | line | Only stack with regular PillarBlocks, NOT AshenKingPillars (they're independent) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 226 | line | First, handle stack enforcement and syncing BEFORE applying NBT data |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 227 | line | This ensures the pillar is in the correct state before we add the custom item |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 231 | line | Now apply NBT data from the placed item (ITEM and PATTERN tags) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 242 | line | Read ITEM tag and set displayed item |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 243 | line | 8 = String type |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 255 | line | Invalid item ID, ignore |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 259 | line | Read PATTERN tag and set particle pattern |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 260 | line | 8 = String type |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 268 | line | Send block update to sync to clients |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 602 | line | Only stack with regular PillarBlocks, NOT AshenKingPillars (they're independent) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 642 | line | Store NBT data in ThreadLocal to be applied in onPlace (after the block is fully initialized) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 645 | line | Check for direct NBT tags (from /give command) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 649 | line | Check for BlockEntityTag (from /fill command or middle-click with NBT) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 664 | line | Only add custom NBT data if the player is sneaking (Creative middle-click behavior) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 671 | line | Write ITEM tag if the pillar has a displayed item |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 684 | line | Write PATTERN tag if the pillar has a pattern |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 691 | line | Only add BlockEntityTag if we have custom data |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 990 | line | Intentionally removed: blockEntity.setParticlePattern(cfg.pattern); |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 991 | line | This allows untouched pillars to naturally inherit future global pattern changes. |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 1038 | line | Only iterate through regular PillarBlocks, NOT AshenKingPillars |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 1069 | line | Only iterate through regular PillarBlocks, NOT AshenKingPillars |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 1092 | line | Stop at AshenKingPillars - they're not part of regular pillar stacks |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlock.java` | 1102 | line | Stop at AshenKingPillars - they're not part of regular pillar stacks |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 81 | line | Per-pillar pattern settings (null means use global config) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 85 | line | Compiled pattern for hex colour validation — avoids re-compiling the regex |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 86 | line | on every call to getParticleColor(). |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 89 | line | Max number of colors for this pillar (1-5, null means use global config) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 99-103 | block | * * Monotonically increasing version counter: incremented on every color/pattern * change so that syncColorsFromManager() can skip its O(n) list-equality * walk when nothing has changed since the last sync. |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 105 | line | version at last successful sync |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 139 | line | Sync every 100 ticks (5 seconds) — colors/patterns change rarely |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 140 | line | PER-PILLAR OVERRIDE for cfg.use_pattern |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 235 | line | Handle "none" pattern - no particles |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 237 | line | Return null to skip particle spawning |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 240 | line | Use pillar-specific pattern settings if available, otherwise use global |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 241 | line | config |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 310 | line | Snowflake pattern: spawn particles 2 blocks above, falling down like rain |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 314 | line | 2 blocks above the pillar |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 316 | line | Slight horizontal drift |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 317 | line | Falling down |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 318 | line | Slight horizontal drift |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 319 | line | Smaller particles for snowflakes |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 331 | line | When use_pattern is false, default to ring pattern |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 380 | line | IMPORTANT: Don't sync until manager has loaded |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 381 | line | Otherwise, patterns loaded from NBT might be overwritten |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 409 | line | Sync pattern |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 426 | line | Sync pattern settings |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 496 | line | Force manager to load when block entity loads to ensure removal detection |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 497 | line | works immediately |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 502 | line | Register this pillar with the manager to ensure it's tracked in the GUI |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 505 | line | Ignore errors during load, recovery will handle it |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 520 | line | Sync with manager |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 530 | line | Force registration of newly placed pillar stack when an item is added |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 543 | line | Sync colors and pattern from manager periodically. |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 544 | line | Both syncs share the same chunk-validity / manager-ready guards, |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 545 | line | so we merge them into a single if-block to halve that overhead. |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 549 | line | single combined sync — avoids duplicate guard checks |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 567 | line | Sync with manager |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 577 | line | Force registration of newly placed pillar stack when an item is added |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 585-590 | block | * * Combined server-side sync: runs the expensive guard checks ONCE and then * syncs both colors and pattern in a single pass. * This replaces the previous pattern of calling syncColorsFromManager() and * syncPatternFromManager() sequential... |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 595 | line | ── Shared guard block ─────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 619 | line | Resolve the pillar ID once |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 632 | line | ── Sync colors ─────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 641 | line | Fine-grained compare only when sizes match |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 662 | line | ── Sync pattern ────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 678 | line | Manager pattern is null - reset to follow global |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 683 | line | Sync pattern settings |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 760 | line | Always orient to bottom for ID consistency |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 765 | line | Sync all world state (item, type, etc.) alongside the pattern update |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 769 | line | This triggers immediate broadcast to all clients |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 775 | line | Returns null for new/default pillars |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 777 | line | If current pattern is null (default fallback state), we want to start cycling |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 778 | line | FROM the default |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 779 | line | So the first click will set it to the first pattern after "default" in the |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 780 | line | array. |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 848 | line | Allow sync even if colors exist - this enables updates from config GUI |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 849 | line | Only skip if we're already synced and nothing changed |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 850 | line | (Original behavior preserved for performance, but now allows forced updates) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 854 | line | IMPORTANT: Don't sync (or clear colors) until manager has loaded |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 855 | line | Otherwise, colors loaded from NBT will be cleared before manager loads |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 898 | line | ID changed, need to sync |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 901 | line | No colors but manager has them |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 904 | line | Check if colors have changed by comparing lists |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 934 | line | Data is null - pillar ID was removed from manager OR doesn't exist yet |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 935 | line | IMPORTANT: NEVER clear colors that exist in NBT |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 936 | line | Colors loaded from NBT are the source of truth for rendering |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 937 | line | Only sync colors TO manager if they exist in NBT |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 940 | line | Colors exist in NBT but manager doesn't have them - sync TO manager |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 944 | line | Create new data entry at bottom position |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 947 | line | If the ID matches, sync colors FROM NBT TO manager |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 949 | line | Only sync if manager doesn't have colors or has different colors |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 954 | line | Check if colors differ |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 971 | line | Sync colors FROM NBT TO manager |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 978 | line | Don't save immediately during sync - let recovery or explicit saves handle it |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 979 | line | Log sync only for debugging |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 980 | line | System.out.println("BuildScape: Synced " + this.particleColors.size() + |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 981 | line | " colors from NBT to manager for " + idToSync); |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 985 | line | DO NOT clear colors here - colors loaded from NBT are preserved |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 986 | line | Only the reset handler should clear colors |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1002 | line | Signals fallback to global config |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1011 | line | Update the pattern in PillarIdManager config |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1231 | line | Important: always orient to foundation for ID consistency |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1234 | line | Use manager's consolidated dyeing logic which also syncs current world |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1235 | line | properties (item/type) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1259 | line | Auto-update maxParticleColor to match current color count |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1329-1335 | block | * * Resets the pillar to default appearance (freshly placed state). * Clears all custom particle colors, patterns, and settings. * Removes the pillar ID association completely. * Keeps the displayed item intact. * This is called when a pill... |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1464 | line | Colors are set, mark as initialized to prevent re-dyeing |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1494 | line | Remove pillar ID association - make it freshly placed |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1497 | line | Reset particle tick to restart particle effects immediately |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1499 | line | Mark as changed so NBT is saved |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1500 | line | When saveAdditional is called, it won't write null fields, effectively |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1501 | line | removing them from NBT |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1504 | line | Force immediate save and sync |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1506 | line | Mark chunk as needing save - this ensures NBT is written with cleared values |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1511 | line | Force block update to sync changes to clients immediately |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1512 | line | This sends the update packet which includes the cleared NBT |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1518-1521 | block | * * Synchronizes ALL settings from PillarData to this block entity. * This includes colors, pattern, speed, spread, and intensity. |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1528 | line | Sync ID |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1534 | line | Sync colors |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1563 | line | Sync pattern |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1572 | line | Sync use_pattern override |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1581 | line | Sync settings |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1638 | line | Handle ITEM tag from /fill or /give commands (custom NBT format) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1674 | line | Handle PATTERN tag from /fill or /give commands (custom NBT format) |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1691 | line | Load pattern settings |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1753 | line | Default: colors are initialized if they exist |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1758 | line | If we have colors loaded from NBT, ensure colorsInitialized is true |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1759 | line | This prevents re-dyeing after world reload |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1840 | line | notify syncer that data changed |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1909 | line | ── Particle reflection cache ─────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1910 | line | Look up the providers map and the internal ParticleEngine.add() once per |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1911 | line | particle type rather than on every spawn tick. Keyed by particle type so |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1912 | line | both GLOW_LIME_SPARKLE and SNOWFLAKE each get their own cached provider. |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1929 | line | Get pattern to determine particle type |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1955 | line | Only queue color for non-snowflake particles |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 1986 | line | ── Cached reflection lookup (runs at most once per particle type) ─────── |
| `src/main/java/com/kingodogo/buildscape/block/PillarBlockEntity.java` | 2030 | line | Only queue color for non-snowflake particles |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 183 | line | Check if the support block is actually gone |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 198 | line | If support is gone, force falling regardless of what's below |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 199 | line | Otherwise, only fall if the space below is free |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 228 | line | Allow falling through replaceable blocks and water (water is replaceable) |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 811 | line | Traverse the entire icicle chain upward to find the source block |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 828 | line | Accept Packed Icicle Block or vanilla Packed Ice as the source block |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 856 | line | Store Packed Icicle Block in the cauldron |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 899 | line | Only accept Icicle Block, not Packed Icicle (for consistency with cauldron feature) |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 1023 | line | Only accept Icicle Block, not Packed Icicle (for consistency with cauldron feature) |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 1060 | line | Accept normal icicle blocks, packed icicle blocks, and vanilla ice blocks as source blocks |
| `src/main/java/com/kingodogo/buildscape/block/PointedIcicleBlock.java` | 1193 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/block/PotentSulfurBlockEntity.java` | 166 | line | Propel upwards towards the top of the cloud |
| `src/main/java/com/kingodogo/buildscape/block/PotentSulfurBlockEntity.java` | 172 | line | Float & bob up and down right at the cloud surface |
| `src/main/java/com/kingodogo/buildscape/block/PotentSulfurBlockEntity.java` | 178 | line | Above target height: allow gravity to bring entity down to cloud top |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 28 | line | Bloom for 2s |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 32 | line | Play sounds |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 35 | line | Particle bloom effect |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 46 | line | Spread Sculk in radius 4-5 around death pos |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 55 | line | Convert natural solid blocks to Sculk |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 57 | line | 65% spread chance per block in radius |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 60 | line | Chance to spawn shrieker, sensor, or sculk vein attached to surrounding solid faces |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 73 | line | Also try placing sculk veins on adjacent open faces around the converted sculk block |
| `src/main/java/com/kingodogo/buildscape/block/SculkCatalystHandler.java` | 104 | line | Attach vein face ONLY if the neighbor in that direction is a sturdy solid face |
| `src/main/java/com/kingodogo/buildscape/block/ShelfBlock.java` | 111 | block | * Vanilla shelves cannot be pushed by pistons. |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyGlassBlock.java` | 84 | line | factory_white_glass -> factory_red_glass |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyGlassBlock.java` | 87 | line | white_mosaic_glass -> red_mosaic_glass |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyGlassBlock.java` | 90 | line | white_glazed_glass -> red_glazed_glass |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyPaneBlock.java` | 116 | line | factory_white_glass_pane -> factory_red_glass_pane |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyPaneBlock.java` | 119 | line | white_mosaic_glass_pane -> red_mosaic_glass_pane |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyPaneBlock.java` | 122 | line | white_glazed_glass_pane -> red_glazed_glass_pane |
| `src/main/java/com/kingodogo/buildscape/block/SilkTouchOnlyPaneBlock.java` | 133 | line | Preserve connections (IronBarsBlock uses North, South, East, West, Waterlogged) |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 36-38 | block | * * Tracks current redstone power — changing this fires observers; value mirrors live signal. |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 140 | line | Notify comparators for the whole stack |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 152-154 | block | * * Comparator output: 15 when smoke is active, 0 when off. Reads from the BlockEntity. |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 162 | line | Read from the top block's entity so any segment next to a comparator returns correctly |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 171-175 | block | * * Sets POWERED on every segment of the pillar, flag=3 so adjacent observers detect the * block state change. The cascade guard in neighborChanged prevents vent segments from * reacting to each other's updates. |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 219 | line | Dye interaction - changes smoke color for the whole stack |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 250 | line | Empty hand right-click to toggle smoke on/off |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 258 | line | VERY IMPORTANT: right-clicking didn't update comparators or observers before! |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 261 | line | Updates comparators |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 262 | line | Updates observers |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 279 | line | Shift + click with water bucket to clear color |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 325 | line | Colored smoke - queue color then spawn custom particle |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlock.java` | 329 | line | Default campfire smoke |
| `src/main/java/com/kingodogo/buildscape/block/SmokeVentBlockEntity.java` | 13 | line | hex color string e.g. "#FF0000", null = default gray smoke |
| `src/main/java/com/kingodogo/buildscape/block/SoftFabricBlock.java` | 73 | line | Hand/other tools: mine fast as well |
| `src/main/java/com/kingodogo/buildscape/block/SoftFabricBlock.java` | 114 | line | fallback if color not found in enum |
| `src/main/java/com/kingodogo/buildscape/block/SpoolBlock.java` | 75 | line | Hand/other tools: mine fast as well |
| `src/main/java/com/kingodogo/buildscape/block/SpoolBlock.java` | 117 | line | fallback if color not found in enum |
| `src/main/java/com/kingodogo/buildscape/block/SpoolBlock.java` | 133 | line | fallback if color not found in enum |
| `src/main/java/com/kingodogo/buildscape/block/StrawBedBlock.java` | 52 | line | Destroy the head block and drop items |
| `src/main/java/com/kingodogo/buildscape/block/StrawBedBlock.java` | 55 | line | Remove the foot block without dropping items (to avoid duplicate drops) |
| `src/main/java/com/kingodogo/buildscape/block/SulfurSpikeBlock.java` | 183 | line | Check if the support block is actually gone |
| `src/main/java/com/kingodogo/buildscape/block/SulfurSpikeBlock.java` | 198 | line | If support is gone, force falling regardless of what's below |
| `src/main/java/com/kingodogo/buildscape/block/SulfurSpikeBlock.java` | 199 | line | Otherwise, only fall if the space below is free |
| `src/main/java/com/kingodogo/buildscape/block/SulfurSpikeBlock.java` | 218 | line | Allow falling through replaceable blocks and water (water is replaceable) |
| `src/main/java/com/kingodogo/buildscape/block/SulfurSpikeBlock.java` | 1080 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 39-48 | block | * * Trapped Decorated Pot – identical feature-set to {@link DecoratedPotBlock} * PLUS a spawn-egg trap mechanic: * <ul> * <li>If the stored item is a {@link SpawnEggItem}, breaking the pot or * right-clicking empty-handed (retrieve gesture)... |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 133 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 134 | line | Interaction |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 135 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 147 | line | ---- Shift + item in hand → store item (same logic as normal pot) ---- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 149 | line | Prevent storing trapped pots inside themselves |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 188 | line | Full or incompatible |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 197 | line | ---- Empty hand, no shift → "poke" wobble ---- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 207 | line | Prevent storing trapped pots |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 213 | line | ---- Item in hand, no shift → store 1 at a time ---- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 252 | line | ---- Empty hand + shift → retrieve (spawn if egg, else give back) ---- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 259 | line | Trap fires: spawn the mob at the top of the pot |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 261 | line | No egg returned – the trap is consumed |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 263 | line | Normal retrieval: give item back |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 285 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 286 | line | Breaking – spawn egg trap fires on break too |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 287 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 297 | line | Trap fires: spawn mob, don't drop egg |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 300 | line | Drop non-egg items normally |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 336 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 337 | line | Helpers |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 338 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 340-342 | block | * * Spawns the mob associated with the given spawn egg directly above the pot. |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 347 | line | Spawn in the center, just above the top of the pot |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlock.java` | 359 | line | Poof particles |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 17-21 | block | * * Block entity for Trapped Decorated Pots. * Stores a single spawn egg item; when the block is right-clicked or broken * the mob is spawned instead of the item being dropped/returned. |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 26 | block | * The spawn egg (or any item) stored inside the pot. |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 44 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 45 | line | Stored-item API |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 46 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 68 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 69 | line | Wobble API (reused by renderer for the same wobble animation) |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 70 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 89 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 90 | line | WorldlyContainer – single-slot, only accepts from top, ejects from bottom |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 91 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 185 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 186 | line | NBT |
| `src/main/java/com/kingodogo/buildscape/block/TrappedDecoratedPotBlockEntity.java` | 187 | line | ------------------------------------------------------------------------- |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 51 | line | Trophy definitions add their blocks and items to these deferred registers. |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 52 | line | Initialize them before either register is attached to the mod event bus. |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 98 | line | Only set base max stack size for POTION item - the ItemMixin restricts this to water bottles only |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 101 | line | Set max stack size for CAKE item to 64 |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 155 | line | Not used - we override execute instead |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 207 | line | Determine which slot of the 3x3 dispenser grid fired the confetti |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 208 | line | Default to center slot (4) |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 225 | line | Calculate horizontal yaw rotation offset based on slot index (0..8) |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 226 | line | Slot 4 (center) = 0 yaw offset (straight out front face) |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 227 | line | Slots 0..3 angle left, slots 5..8 angle right |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 230 | line | Main direction is always out of the front face of the dispenser |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 235 | line | Right vector relative to front face for horizontal yaw rotation |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 314 | line | Potted Plants Registration |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 433 | line | Cauldron interactions for Empty Cauldron |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 462 | line | Cauldron interactions for Experience Cauldron |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 559 | line | Reset the in-memory cache - file data will be loaded when first player joins |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 653 | line | Use forceSaveImmediate because at this point all players have left |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 654 | line | and saveImmediate() would skip due to playerCount==0 guard |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 677 | line | REMOVED: Don't reset pillar data on player join - it clears saved pillar IDs! |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 678 | line | PillarIdJoinSyncHandler will load the data if needed |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 680 | line | com.kingodogo.buildscape.config.PillarIdManager.resetWorldCache(); |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 717 | line | IMPORTANT: Sync pillar IDs to client so GUI works on servers |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 718 | line | Use a robust delayed sync that actually waits for manager to be ready |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 721 | line | Also Sync Gamerules |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 732 | line | Schedule pillar ID sync - try immediately, with retries if not loaded |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 760-763 | block | * * Schedule pillar ID sync to a player with retry logic. * Uses the async pool to wait for the manager to load, then dispatches on the main thread. |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 772 | line | Give up after max attempts — send whatever we have |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 781 | line | Manager ready — sync immediately on main thread |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 787 | line | Not loaded yet — wait on async pool and retry |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 798-800 | block | * * Sends all pillar ID data to a specific player. |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 805 | line | Ensure latest colors from NBT before sending |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 846 | line | Use forceSaveImmediate - players may have already left |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 1599 | line | Collect mist from air |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2780 | line | Target any non-hostile AgeableMob (passive animals, villagers, etc.) |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2790 | line | Freeze: lock the mob's current life stage |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2800 | line | Unfreeze: resume normal aging |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2827 | line | Baby: lock age so it never grows into an adult |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2830 | line | Adult: lock age at 0 so the breeding cooldown never accumulates |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2838 | line | Apply swimming speed boost in XP fluid (reduced by 50%) |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2847 | line | Apply geyser launch force to players and other living entities (fixes players not being launched) |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2851 | line | Look down up to 24 blocks below the entity |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2857 | line | Find the source block (top of water column) |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2879 | line | Blocked by a solid block |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2929 | line | 1. Bonemeal Cactus Top -> Cactus Flower |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2944 | line | 2. Bonemeal Sand -> Single-block Dry Grass |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2959 | line | 3. Bonemeal Grass Block -> Generates Bushes in radius alongside vanilla grass, ferns, etc. |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2962 | line | Use a delayed task so vanilla vegetation (grass, fern, flowers) spawns first |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2976 | line | 4. Bonemeal Moss Block -> Generates Firefly Bushes, Cherry Saplings & Mangrove Propagules alongside vanilla Moss Block bonemeal |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2979 | line | Use a delayed task so vanilla moss vegetation (grass, azalea, etc.) spawns first |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2985 | line | 10% chance: Firefly Bush |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2987 | line | 5% chance: Cherry Sapling |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 2992 | line | 5% chance: Mangrove Propagule |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 3004 | line | 4. Bonemeal Pale Oak Leaves -> Pale Hanging Moss |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 3022 | line | 5. Bonemeal Creaking Heart -> Spawns Resin Clump blocks on heart block faces |
| `src/main/java/com/kingodogo/buildscape/BuildScape.java` | 3057 | line | 7. Bonemeal Tall Dry Grass -> Drops Short Dry Grass item |
| `src/main/java/com/kingodogo/buildscape/client/BuildscapeRenderLayers.java` | 8-12 | block | * * Registers non-solid render layers in one pass over Buildscape's blocks. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/client/ClientAdvancementEvents.java` | 42 | line | SHIFT + Scroll: Horizontal Scroll (Left / Right) |
| `src/main/java/com/kingodogo/buildscape/client/ClientAdvancementEvents.java` | 49 | line | Normal Scroll: Vertical Scroll (Up / Down) |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 99 | line | 5 seconds duration (5000 ms) |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 106 | line | Move from crosshair to subtitle position |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 109 | line | Translate Z first to be safe |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 111 | line | Pop animation |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 116 | line | Pop in (0 to 0.25s) |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 119 | line | Settle back to 1.0 (0.25s to 0.4s) |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 134 | line | Red with full Alpha |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 148 | line | Check for particle shape reloads |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 149 | line | ParticleShapeReloader.tick(); |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 156-161 | block | // ── Guidebook keybind ───────────────────────────────────────────── if (mc.screen == null && ModKeyBinds.OPEN_GUIDEBOOK.consumeClick()) { mc.setScreen(new com.kingodogo.buildscape.client.guidebook.screen.GuideBookScreen()); } |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 226 | line | IMPORTANT: Reset PillarIdManager cache when disconnecting from server |
| `src/main/java/com/kingodogo/buildscape/client/ClientEvents.java` | 227 | line | This ensures that when you rejoin, all pillar data is properly synced again |
| `src/main/java/com/kingodogo/buildscape/client/CopperChestClientEvents.java` | 10-14 | block | * * Registers copper chest sprites with Minecraft's dedicated chest atlas. * * @author HoYin1600p |
| `src/main/java/com/kingodogo/buildscape/client/event/ClientFluidEvents.java` | 21 | line | Lime fog colors |
| `src/main/java/com/kingodogo/buildscape/client/GeyserParticleHandler.java` | 19-22 | block | * * Spawns geyser particles for sulfur blocks beyond vanilla block entity ticker range (~16 blocks). * Runs lightweight chunk scanning once per second (every 20 ticks) to avoid CPU overhead. |
| `src/main/java/com/kingodogo/buildscape/client/GeyserParticleHandler.java` | 41 | line | Only scan once per second (every 20 ticks) to eliminate CPU lag |
| `src/main/java/com/kingodogo/buildscape/client/GeyserParticleHandler.java` | 65 | line | Skip sulfur blocks within vanilla ticker range — those are handled by BlockEntityTicker |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 48 | line | Must have a block in offhand |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 59 | line | Cancel default outline box |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 74 | line | Pick color based on hammer tier |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 84 | line | Steel silver |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 85 | line | Cyan diamond |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 86 | line | Dark red |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 93 | line | Base bounding box |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 101 | line | Slightly inflated outer glow for vibrant visual feedback |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 113 | line | Right-click (button 1) to replace block |
| `src/main/java/com/kingodogo/buildscape/client/HammerClientHandler.java` | 125 | line | Must have a block in offhand |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 34 | line | 1 chunk |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 49 | line | Must be sneaking and holding an invisible item frame |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 64 | line | Find all invisible item frames within 1 chunk range (vanilla + modded) |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 82 | line | Calculate pulsing alpha (0.4 to 0.8 for lines, 0.1 to 0.3 for fill) |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 88 | line | Light cyan color |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 93 | line | Render filled faces first (using lightning for a translucent, non-culled effect) |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 102 | line | Render wireframe |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 127 | line | Down |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 133 | line | Up |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 139 | line | North |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 145 | line | South |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 151 | line | West |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 157 | line | East |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 179 | line | Bottom face edges |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 185 | line | Top face edges |
| `src/main/java/com/kingodogo/buildscape/client/InvisibleFrameOverlayRenderer.java` | 191 | line | Vertical edges |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 35 | line | 1. Body and its children (head, arms, backpack, spyglass) |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 37 | line | body upper |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 38 | line | body lower / robe |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 39 | line | backpack main |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 40 | line | backpack top |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 41 | line | spyglass body |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 42 | line | spyglass lens |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 45 | line | 2. Head and its children (nose, hat, brim/cube) |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 47 | line | head |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 48 | line | hat |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 59 | line | 3. Arms (child of body) |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 61 | line | arms center |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 62 | line | right arm |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 63 | line | left arm |
| `src/main/java/com/kingodogo/buildscape/client/model/WanderingHomemakerModel.java` | 66 | line | 4. Legs (root children) |
| `src/main/java/com/kingodogo/buildscape/client/ModKeyBinds.java` | 20-29 | block | Opens the BuildScape Guidebook. Default key: G public static final KeyMapping OPEN_GUIDEBOOK = new KeyMapping( "key.buildscape.open_guidebook", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_G, CATEGORY ); |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 44 | line | Only render when holding the muff block in offhand |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 64 | line | Offset relative to camera |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 70 | line | Cyan color: R=0, G=220, B=255, A=180 |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 97 | line | Draw the 12 edges of the bounding box |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 98 | line | Bottom face (4 edges) |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 104 | line | Top face (4 edges) |
| `src/main/java/com/kingodogo/buildscape/client/MuffBlockRenderer.java` | 110 | line | Vertical edges (4 edges) |
| `src/main/java/com/kingodogo/buildscape/client/performance/BuildscapeBlockStateCacheCoordinator.java` | 15-20 | block | * * Defers only Buildscape block-state caches until the block registry bake can * calculate them in parallel, then returns with every cache fully initialized. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/client/performance/BuildscapeStartupWork.java` | 13-18 | block | * * Runs bounded, short-lived startup work without occupying Minecraft's shared * background executor. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/client/performance/LaunchFasterInterop.java` | 11-16 | block | * * Reads optional LaunchFaster settings without introducing a dependency on * that mod. Buildscape yields only when LaunchFaster owns the same operation. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 9-12 | block | * * Client-side manager to track marked pillars that should display blinking borders. * Marks expire after 15 seconds. |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 15 | line | 4.5 seconds |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 42 | line | Blink every 500ms (on/off cycle) |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 47-50 | block | * * Get the color for the gradient (yellow to red) * Returns a value from 0.0 (yellow) to 1.0 (red) based on elapsed time |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 53 | line | Gradually transition from yellow (0.0) to red (1.0) over the duration |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 58-60 | block | * * Mark a pillar to display blinking borders. |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 65-67 | block | * * Check if a pillar at the given position is marked. |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 71 | line | Clean up expired marks first |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 87-89 | block | * * Get all marked pillars in the given dimension. |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 101-103 | block | * * Remove expired marks. |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerManager.java` | 113-115 | block | * * Clear all marks (useful for testing or cleanup). |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 22-24 | block | * * Renders blinking borders around marked pillars in the world. |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 31 | line | Only render within 64 blocks |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 36 | line | Only render after particles (which happens after entities) |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 68 | line | Render bounding boxes for marked pillars |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 76 | line | Check distance |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 86 | line | Find the full pillar stack (connected pillars above and below) |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 90 | line | Render bounding box for the entire stack |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 94 | line | Suppressed debug log to prevent render loop spam |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 140 | line | Don't render if too transparent |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 145 | line | Calculate the bounding box for the entire stack |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 153 | line | Offset by camera position for rendering |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 158 | line | Calculate color with yellow-red gradient and alpha (blinking effect) |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 159 | line | 0.0 = yellow, 1.0 = red |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 160 | line | Yellow: RGB(255, 255, 0), Red: RGB(255, 0, 0) |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 164 | line | Apply alpha |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 172 | line | Draw the 12 edges of the bounding box |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 173 | line | Bottom face (4 edges) |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 179 | line | Top face (4 edges) |
| `src/main/java/com/kingodogo/buildscape/client/PillarMarkerRenderer.java` | 185 | line | Vertical edges (4 edges) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 22-25 | block | * * Dedicated renderer for armor displayed on pillars using an ArmorStand. * Modeled after MobPillarRenderer for consistency and robustness. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 46 | line | Get or create cached ArmorStand |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 55 | line | Force update |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 57 | line | Failed to create |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 61 | line | Identify logic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 65 | line | Update Equipment / State if changed or forced |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 74 | line | Render Setup |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 77 | line | Calculate Scale and Offset |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 81 | line | Pillar Top is at height 1.0 from block base. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 82 | line | PoseStack starts at standard item center 1.46 from block base. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 83 | line | Pillar top relative to PoseStack: 1.0 - 1.46 = -0.46. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 84 | line | We want the "bottom" of the armor piece to be at -0.42 (slight 4px gap). |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 92 | line | Adjust yOffset so the part's bottom aligns with baseOffset |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 93 | line | These values are the Y-height of the model part's bottom in the ArmorStand model |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 99 | line | Legs and Feet cover the lower parts, which go all the way down to 0.0 on an armor stand. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 100 | line | We use baseOffset directly to align the bottom of the model with the pillar top. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 110 | line | Entity Rotation |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 111 | line | Reset local rotations on entity to avoid accumulation |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 119 | line | Render Entity using standard dispatcher but with robust state |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 122 | line | If we want to guarantee rendering, we trust dispatcher.render but ensure parameters are clean. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 123 | line | We pass 0,0,0 because PoseStack handles position. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 142 | line | Initial NBT Setup |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 158 | line | Clear slots first |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 164 | line | Prepare to flip invisible/noBasePlate bits |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 167 | line | Since we are updating specific flags, we can just pass them to readAdditionalSaveData. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 168 | line | The method merges/overwrites fields present in the tag. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 177 | line | Equip Item Logic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ArmorPillarRenderer.java` | 188 | line | NBT read error - entity may be corrupt |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 63 | line | Fix dark/black rendering on ceiling and floor by sampling light from the open-air side |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 71 | line | Undo the render offset applied by EntityRenderDispatcher, matching vanilla |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 75 | line | Translate slightly more than vanilla (0.46875) to avoid Z-fighting with the block behind |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 83 | line | Apply rotation using entity's xRot/yRot (set by setDirection), matching vanilla |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 91 | line | Skip frame rendering if invisible (item still renders), matching vanilla |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 101 | line | Render the item if present |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 116 | line | Back panel: from [3, 3, 15.5] to [13, 13, 16] in pixel coords (0-16 maps to 0-1) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 120 | line | Frame border z positions: from 15 to 16 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 124 | line | Render back panel with colored texture |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 127 | line | Back panel - front face (facing the viewer) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 136 | line | Back panel - back face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 145 | line | Render frame border |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 148 | line | Bottom border: [3, 2] to [13, 3] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 153 | line | Top border: [3, 13] to [13, 14] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 158 | line | Left border: full height [2, 14] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 163 | line | Right border: full height [2, 14] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 178 | line | Back panel: from [1, 1, 15.001] to [15, 15, 16] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 182 | line | Frame border |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 186 | line | Render back panel with colored texture |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 189 | line | Back panel - front face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 198 | line | Back panel - back face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 207 | line | Render frame border |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 210 | line | Bottom border: [1, 0] to [15, 1] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 215 | line | Top border: [1, 15] to [15, 16] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 220 | line | Left border: full height [0, 16] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 225 | line | Right border: full height [0, 16] |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 233-235 | block | * * Renders all 6 faces of a box defined by two corners. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 244 | line | Front face (-Z) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 250 | line | Back face (+Z) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 256 | line | Bottom face (-Y) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 262 | line | Top face (+Y) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 268 | line | Left face (-X) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 274 | line | Right face (+X) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 306 | line | When invisible, item sits at 0.5 (block face); otherwise 0.4375 (in front of frame surface) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ColoredItemFrameRenderer.java` | 309 | line | Apply rotation based on item rotation value (0-7) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/DecoratedPotBlockEntityRenderer.java` | 101 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/EmbeddiumSpillBuffer.java` | 10 | block | * Bridges the outlet's Forge water mesh into Embeddium's chunk buffers. Author: HoYin1600p. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/FallingIcicleRenderer.java` | 85 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/FestiveStockingBlockEntityRenderer.java` | 168 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/FestiveStockingRenderer.java` | 142 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 86 | line | 1. Render the jar block model itself (animated with wobble) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 99 | line | 2. Render stored liquid if present |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 103 | line | 3. Render stored food items if present |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 232 | line | Top face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 238 | line | North face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 244 | line | South face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 250 | line | West face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/GlassJarBlockEntityRenderer.java` | 256 | line | East face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 77 | line | 1. Render Contained Fluid in Hollow Pipe or Hollow Log |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 88 | line | 2. Render Nested / Inset Decoration Block |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 108 | line | 3. Render Glass Cover Negative Face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 116 | line | 4. Render Glass Cover Positive Face |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 154 | line | Use vanilla's flowing-water levels (7/9 through 1/9) so the internal |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 155 | line | channel falls in the same sequence as the world water emitted at its |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 156 | line | downstream endpoint. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 173 | line | Small inward offset to prevent Z-fighting where water quads would be |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 174 | line | coplanar with the pipe's inner wall geometry (0.125 / 0.875 faces). |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 175 | line | ~0.5 pixel inward bias |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 187 | line | A source pipe with a single downstream direction is flowing too. Using |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 188 | line | water_flow here keeps the animated flow texture continuous from |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 189 | line | the first channel segment through the final world-water block. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 190 | line | A stationary waterlogged pipe flowing symmetrically to both ends uses water_still. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 226 | line | Full 1.0F vertex alpha to match vanilla Minecraft water rendering |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 260 | line | Unsquished 1:1 UV mapping along flow direction |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 266 | line | Top surface |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 279 | line | West face (only if open exit to air and neighbor is not fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 289 | line | East face (only if open exit to air and neighbor is not fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 325 | line | Unsquished 1:1 UV mapping along flow direction |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 331 | line | Top surface |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 344 | line | North face (only if open exit to air and neighbor is not fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 354 | line | South face (only if open exit to air and neighbor is not fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 366 | line | Generic Junction / Vertical Column |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 457 | line | 1. Neighbor is a HollowPipeBlock: |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 469 | line | 2. Neighbor is a HollowLogBlock: |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 481 | line | 3. Neighbor is a world fluid block: |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 491 | line | 4. Neighbor is air at an open pipe endpoint: |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 494 | line | Pipe floor drop |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 497 | line | Solid block or closed wall: |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 626 | line | 0.25 pixel inward |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 627 | line | 0.921875D |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 646 | line | Y |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 715 | line | Full 1.0F vertex alpha to match vanilla Minecraft water rendering |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 734 | line | Top surface (1:1 UV mapped, no squishing) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 747 | line | North end cap (only if glass or open to air / not neighbor fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 755 | line | South end cap (only if glass or open to air / not neighbor fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 771 | line | Top surface (1:1 UV mapped, no squishing) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 784 | line | West end cap (only if glass or open to air / not neighbor fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 792 | line | East end cap (only if glass or open to air / not neighbor fluid) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 799 | line | Y axis |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 808 | line | Top surface |
| `src/main/java/com/kingodogo/buildscape/client/renderer/HollowLogBlockEntityRenderer.java` | 823 | line | Bottom surface |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 41 | line | Render the cauldron block using vanilla cauldron model |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 65 | line | Render the icicle block inside the cauldron if present |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 76 | line | Cauldron interior: x/z from 0.0625 to 0.9375 (14 pixels wide), y from 0.25 to 1.0 (12 pixels tall) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 77 | line | Block should be 1 pixel short on all sides: 12 pixels wide (0.75 scale), 10 pixels tall (0.625 scale) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 78 | line | Transformations: translate first (applies second), scale second (applies first) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 82 | line | Render the icicle block |
| `src/main/java/com/kingodogo/buildscape/client/renderer/IcicleCauldronBlockEntityRenderer.java` | 112 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MangroveBoatRenderer.java` | 38 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 24-30 | block | * * Dedicated renderer for mob entities displayed on pillars. * Handles state parsing from spawn egg names and applies corresponding visual/behavioral modifications. * <p> * This class is designed to be modular and maintainable, allowing ne... |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 33 | line | Cache for entity instances to avoid creating new ones every frame |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 36 | line | Cache for last applied states to prevent re-applying NBT every frame |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 37 | line | Key is Entity.getId() (instance ID), Value is the MobState that was applied |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 39-42 | block | * * Apply mob variant data to NBT before entity creation * Uses correct 1.18.2 NBT tags for all supported mobs |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 43 | line | Standard Dye Colors Map for easy lookup |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 46 | line | Cleanup caches if they get too large to prevent memory leaks |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 48 | line | Optional: Add a shutdown hook or periodic cleanup if needed |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 70-72 | block | * * Render a mob entity on a pillar with the specified states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 95 | line | Parse states from spawn egg name |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 98 | line | Create cache key |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 102 | line | Get or create cached entity |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 113 | line | When created, state is inherently applied via createEntity -> applyVariantToNBT |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 114 | line | But applyStates does more (AI disabling etc), so we should run it once. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 115 | line | We'll let the logic below handle it. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 120 | line | Check if state has changed or hasn't been applied fully yet |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 125 | line | Apply states to entity (expensive operation with NBT) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 130 | line | Update entity position and rotation (cheap operation, done every frame) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 136 | line | Render the entity |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 141-143 | block | * * Create a new entity instance with initial setup |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 145 | line | Handle Zombie -> Giant conversion (but NOT for Rabbits) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 151 | line | For rabbits, "giant" should NOT convert to a different entity type |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 152 | line | The scaling happens during rendering based on parsed states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 154 | line | Create NBT with variant data BEFORE creating entity |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 158 | line | Apply variant NBT before entity creation |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 161 | line | Create entity from NBT (this applies variants during creation) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 165 | line | Fallback to normal creation if NBT creation fails |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 173 | line | Basic setup |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 183 | line | Apply glowing effect |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 188 | line | Apply fire effect |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 191 | line | Ensure fire flag is set in data manager for client-side rendering |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 192 | line | This is handled by setSecondsOnFire internally |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 195 | line | Apply frozen effect |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 203-205 | block | * * Apply state-specific modifications to the entity |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 207 | line | Reset tick count to prevent animations |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 210 | line | Force critical visual flags every frame using public methods where possible |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 212 | line | Keep it burning |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 213 | line | If this doesn't work, we need to access the data tracker directly, |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 214 | line | but setSecondsOnFire(>0) sets the flag in base tick usually. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 215 | line | Since we don't tick, we must force the flag. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 216 | line | Best way without reflection: |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 217 | line | We will rely on NBT load being correct, OR we can try: |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 218 | line | entity.clearFire() then entity.setSecondsOnFire(1)? |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 219 | line | Actually, let's use the reflection helper below if needed. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 220 | line | For now, let's assume the NBT fix in createEntity works for initial load. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 221 | line | But if specific frame updates clear it (like if we accidentally tick it), we lose it. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 222 | line | We do NOT call entity.tick(). |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 235 | line | Hurt state - Force red flash |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 239 | line | Ensure not dying |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 242 | line | Disable AI |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 247 | line | Reset animation states (only if not hurt to allow flash) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 262 | line | Robust Baby State Application |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 266 | line | Standard Animals / Villagers |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 272 | line | Zombies and variants (Husk, Drowned, Zombified Piglin) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 273 | line | Note: Zombie is not AgeableMob in 1.18 inheritance tree |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 279 | line | Zoglins |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 285 | line | AbstractPiglin (Piglin, Piglin Brute) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 286 | line | Need to use reflection or check class name if not imported |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 289 | line | Piglins usually have setBaby or setIsBaby |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 297 | line | Fallback for Modded Entities: Try to find setBaby via reflection |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 303 | line | Try setIsBaby |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 308 | line | No baby method found, relying on NBT IsBaby tag |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 309 | line | System.out.println("Could not finding setBaby method for " + livingEntity.getClass().getName()); |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 316 | line | CRITICAL: Force update of entity NBT from state to handle variants like CatType/Saddle |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 317 | line | Only done once per state change now! |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 320 | line | Entity-specific states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 324-326 | block | * * Apply entity-specific state modifications |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 328 | line | Bees |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 337 | line | Wolves |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 350 | line | Cats |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 360 | line | Foxes |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 367 | line | Creepers |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 369 | line | net.minecraft.world.entity.monster.Creeper creeper = (net.minecraft.world.entity.monster.Creeper) entity; |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 371 | line | Note: Setting powered state requires NBT manipulation or reflection |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 372 | line | For now, this state is recognized but not visually applied |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 373 | line | NBT update in applyVariantToNBT handles this for creation/update |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 377 | line | Sheep |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 384 | line | Bat - Roosting/Hanging |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 389 | line | Polar Bear - Standing |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 394 | line | Enderman - Screaming/Staring |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 396 | line | setCreepy is usually client-side visible |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 397 | line | Check mapping name if needed, but setCreepy usually exists? No, it's 'hasBeenStaredAt' logic or data tracker. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 398 | line | 1.18.2 Enderman uses DATA_CREEPY (18). |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 399 | line | We need to verify if setCreepy exists or strict NBT/DataTracker needed. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 400 | line | Actually, let's assume standard accessors exist or verify later. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 401 | line | If error, we might need reflection or specialized handling. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 402 | line | For now, let's check NBT approach: AngerTime > 0 usually makes them scream? |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 404 | line | Force anger state visually? |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 405 | line | enderman.setBeenStaredAt(); |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 409 | line | Spider - Climbing |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 414 | line | Vex - Charging |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 422-424 | block | * * Apply mob variant data to NBT before entity creation |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 432 | line | --- Universal Tags --- |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 439 | line | Visual Flags |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 444 | line | For client-side rendering, TicksFrozen must be set in NBT to init the data tracker correctly |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 447 | line | Handedness (if user adds "lefty" or "left_handed" to states) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 452 | line | Age (Baby/Adult) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 461 | line | --- Mob Specific Logic --- |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 463 | line | Tameable Logic (Wolf, Cat, Parrot) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 474 | line | --- Generic Mod Support: Apply common states optimistically --- |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 475 | line | These tags are harmless if the entity doesn't support them, but enable modded support. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 480 | line | Chested Horse / Donkey / Mule / Llama generic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 483 | line | Optimistic Color Application (Sheep, Shulker, Collar for Tames) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 486 | line | Only apply generic Color if not later handled specifically (though usually safe) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 492 | line | --- Mob Specific Logic --- |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 511 | line | Color handled by generic logic above |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 515 | line | Cold/Shivering requires boolean |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 516 | line | Note: Striders rely on environment for shivering, but we can't force it via NBT easily without environment. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 517 | line | However, we can set 'Suffocating' to true via reflection or specific entity NBT if available? |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 518 | line | Actually 1.18 striders shiver if on land. Since pillars are air/land, they should shiver by default? |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 519 | line | No, they shiver if NOT in lava. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 520 | line | We will let them be normal unless 'cold' explicitly requested? No, usually they shiver. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 528 | line | Force spell casting pose |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 532 | line | Default to grass block if carrying |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 582 | line | ChestedHorse handled by generic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 589 | line | Armor Logic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 612 | line | ChestedHorse handled by generic logic above |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 614 | line | Saddle Logic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 643 | line | Size variants |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 644 | line | Default Small |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 645 | line | Smallest |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 647 | line | Big |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 648 | line | Bigger |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 649 | line | Massive |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 654 | line | Low health shows cracks |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 657 | line | Variant is an int packed with size/pattern/body color/pattern color |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 658 | line | This is complex, so we will implement basic presets references |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 659 | line | For now, let's just support a few common ones if requested, or random if not specified? |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 660 | line | Vanilla defaults to random. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 681 | line | Renders blue shield |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 685 | line | Lower health shows cracks (Max 100) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 686 | line | High cracks = Low health |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 704 | line | Default to plains/none if not specified, but keep existing logic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 713-716 | block | * * Looks for a standard dye color in the state and returns its ID. * Returns defaultValue if no color is found. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 753 | line | Ignore NBT errors to prevent crash |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 757 | line | --- Helper Methods --- |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 760 | line | 0=tabby, 1=black, 2=red, 3=siamese, 4=british, 5=calico, 6=persian, 7=ragdoll, 8=white, 9=jellie, 10=all_black |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 797 | line | Colors: 0=White, 1=Creamy, 2=Chestnut, 3=Brown, 4=Black, 5=Gray, 6=Dark Brown |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 869-871 | block | * * Update entity position and rotation based on states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 880 | line | Calculate final yaw |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 892 | line | Update rotation |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 897 | line | Update living entity rotations |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 908 | line | Add floating animation |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 912 | line | Sync tickCount for animated features (like jeb_ sheep or idle anims) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 913 | line | 20 ticks per second for standard Minecraft timing |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 914 | line | Only apply "rainbow" animation/tick advancement to sheep as requested |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 939-941 | block | * * Render the entity with appropriate transformations |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 950 | line | Calculate scale |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 956 | line | Explicitly requested GIANT size. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 957 | line | Special case: Rabbits with "giant" should be bigger than normal |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 959 | line | Make rabbits much bigger when renamed "giant" |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 961 | line | Massive entities (Giants) become ~4.8m |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 963 | line | Smaller entities (Slimes) become prominent |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 970 | line | Explicit small slime/cube should carry some size |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 973 | line | Tiny slime (Size 0) is very small naturally |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 976 | line | Standard Auto-Scaling Logic |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 977 | line | Babies should be smaller overall to maintain proportions (prevents "giant head" effect) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 983 | line | Cap baby scale lower than adults to prevent extreme scaling of tiny entities |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 987 | line | Maintain scaling ratio for large entities relative to target size |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 996 | line | Get entity renderer |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1004 | line | Apply scale |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1007 | line | Apply upside-down rotation |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1015 | line | Render |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1029-1031 | block | * * Clear cached entity for a specific position |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1046-1048 | block | * * Clear all cached entities |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1059-1061 | block | * * Clean up stale entities |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1072 | line | Also clean up lastAppliedStates for IDs that are no longer in known entities |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1073 | line | This is a bit expensive so maybe just clear it periodically or rely on removal hooks above |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobPillarRenderer.java` | 1074 | line | For now, let's keep it simple and safe |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobState.java` | 7-9 | block | * * Data holder for mob variant states parsed from spawn egg names. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 19 | line | Parsed state definitions from mob_states.txt or states.txt |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 24-29 | block | * * Load and parse the states configuration file. * Supports both formats: * 1. **MobName** header with bullet points * 2. mob_name\|state_name\|description pipe-delimited |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 36 | line | Add universal states first (always available) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 39 | line | Try to load from root directory first (user's custom file) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 46 | line | Fallback to resource file |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 66 | line | Skip empty lines |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 71 | line | Format 2: mob\|state\|desc (Pipe-delimited) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 78 | line | Handle combined states (e.g. "baby spin") by taking the first word or checking known states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 79 | line | For simplicity, we add the full string and individual words if separated |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 89 | line | Also handle wildcard mob "*" |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 97 | line | Format 1: **MobName** header |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 100 | line | Normalize mob names (remove spaces, handle special cases) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 105 | line | Format 1: • stateName bullet point |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 109 | line | Skip section headers like "Biomes:" or "Professions:" |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 129 | line | Ensure at least universal states are loaded |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 146 | line | Entity Specific States |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 163 | line | 1.19+ but harmless |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 165 | line | Armor Stand / End Crystal Utils |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 172 | line | Add common states that should universally apply (especially for modded support) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 178 | line | Add all dye colors as universal states (for wool, collars, shulkers, horses, rabbits, etc.) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 200 | line | Size variants |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 203 | line | Size 4 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 204 | line | Size 2 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 206 | line | Size 1 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 208 | line | Specific vanilla variants |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 209 | line | Wither |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 211 | line | Snow Golem |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 213 | line | Iron Golem |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 215 | line | Pufferfish |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 218 | line | Horse/Llama chest |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 221 | line | New Additions |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 222 | line | Sheep jeb_ |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 224 | line | Vindicator |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 225 | line | Strider / Frog |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 227 | line | Frog |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 229 | line | Enderman |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 231 | line | Evoker / Illusioner |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 234 | line | Horse Armor |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 239 | line | Generic armor keyword |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 246-248 | block | * * Parse the spawn egg's custom name to extract mob states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 256 | line | Get the custom name from NBT |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 262 | line | Ensure states are loaded |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 265 | line | Get mob type name |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 272 | line | Parse the custom name for state keywords |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 275 | line | Pre-processing for combined keywords (e.g. "no pumpkin" -> "nopumpkin") |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 282 | line | Check each word against valid states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 289 | line | Add to parsed states for variant checking |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 292 | line | ... (Optimization: Skip isValidState check if we are just parsing broadly) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 294 | line | Apply state based on keyword |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 295 | line | We use parsedStates set for most things now, but keep bools for common ones |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 330-332 | block | * * Resolve aliases for state names (e.g., "sit" -> "sitting") |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 348 | line | Comprehensive aliases for relatability |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 368 | line | Strict mapping for 'no' prefix |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 377-379 | block | * * Extract custom name from spawn egg ItemStack |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 410 | line | Fallback to raw string |
| `src/main/java/com/kingodogo/buildscape/client/renderer/MobStateParser.java` | 417 | line | Explicitly expose clear cache methods if needed, essentially just reloading states |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 27 | line | Client-side timers for smooth rotation animation (completely independent of server) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 28 | line | Maps block position to the time when the item was first rendered on client |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 31 | line | Track the displayed item hash to detect when item changes (reset timer) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 35 | line | ── Per-item render-state caches (keyed by item NBT hash) ───────────────── |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 36 | line | hasItemNameTag / isFixed / hasUpsideDownName all parse JSON every frame; |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 37 | line | cache the result so we only pay that cost when the item actually changes. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 42 | line | isAshenKing is block-type dependent, constant per position — cache it. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 51 | line | Cache for model bounds to avoid recalculating every frame |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 62 | line | also evict block-type cache for this pos |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 85 | line | Safety check - ensure block entity is valid |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 100 | line | If item is removed, clear the timer and hash |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 107 | line | Distance-based culling: skip rendering entities on pillars that are beyond |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 108 | line | the player's current render distance. This prevents off-screen pillars from |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 109 | line | burning CPU/GPU time on entity rendering. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 113 | line | Convert chunk render distance to block distance (each chunk is 16 blocks). |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 114 | line | We use the block-diagonal distance so corners are checked correctly. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 128 | line | Compute hash once — reused by all three per-item caches below |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 131 | line | Check if the item is a spawn egg to determine position and rotation speed |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 135 | line | isAshenKing is constant per position (block type never changes) — cache it |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 147 | line | Smooth rotation animation using PURELY CLIENT-SIDE time. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 148 | line | Items: Always rotate (90 deg/sec). Mobs: only if named "spin". |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 150 | line | Cache isFixed lookup per item NBT hash — avoids repeated JSON parsing |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 159 | line | Cache MobState parse per item NBT hash — avoids repeated JSON/NBT parsing |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 169 | line | Use System.currentTimeMillis() for smooth client-side animation — avoids nanoTime division |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 172 | line | Check if the displayed item has changed - if so, reset the timer |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 176 | line | Item changed or first time rendering - reset timer |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 183 | line | Calculate elapsed time in seconds since item was first rendered |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 186 | line | Calculate rotation based on elapsed time (completely client-side) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 189 | line | Use elapsed time for floating animation as well |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 192 | line | For items, apply rotation to pose stack |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 193 | line | For mobs, rotation will be handled by entity's rotation values |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 196 | line | Fixed items don't spin, they face the pillar's direction |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 204 | line | Add a slight floating animation (bobbing up and down) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 206 | line | Bob up and down |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 210 | line | Check if item is named "item" to render as normal small item |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 213 | line | 3D Gear Detection |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 237 | line | Check if the item is a spawn egg |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 240 | line | Use the new MobPillarRenderer for modular state-based rendering |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 255 | line | If rendering fails, just render as regular item to prevent crashes |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 273 | line | Render item normally |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 275 | line | Revert to FIXED transform to guarantee correct positioning. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 276 | line | The "white outline" is a lighting artifact of FIXED mode, but NONE mode causes unfixable positioning errors. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 277 | line | We prioritize the correct 70/30 positioning logic here. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 298 | line | Base position for standard sword |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 301 | line | Dynamic Adjustment: 70% of EXTRA length sticks OUT. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 302 | line | We use visualLength and standardLength from outer scope |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 306 | line | Dynamic Collision Check |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 320 | line | Axes should look "chopped" into the pillar (like the sword) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 352 | line | Standard floating item rendering |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 374 | line | If rendering fails, make sure to pop the pose stack to prevent issues |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 378 | line | Ignore if pop fails |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 392 | line | Check all sides + null side |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 398 | line | Vertex data format: [x, y, z, color, u, v, ...] (usually IVertexBuilder format) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 399 | line | Default format is usually Position (3 floats) ... |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 400 | line | Unpacking raw data relies on DefaultVertexFormat.BLOCK usually. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 401 | line | Standard baked quad stores data as int array. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 402 | line | Position 3 floats * 4 bytes? No, vertices is int[]. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 403 | line | DefaultVertexFormat.BLOCK: Position(3F), Color(4UB), UV(2F), UV2(2S), Normal(3B), Padding(1B) = 32 bytes = 8 ints. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 404 | line | Position is at offset 0, 1, 2. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 406 | line | 4 vertices per quad |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 422 | line | Fallback for empty models |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 435 | line | Must be a spawn egg (checked by caller, but double-check here) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 440 | line | Check NBT directly for display.Name tag (same approach as dye detection) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 446 | line | Check if display compound exists |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 447 | line | 10 = TAG_COMPOUND |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 452 | line | 8 = TAG_STRING |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 456 | line | Get the name JSON string |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 462 | line | Parse the JSON text component to get plain text |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 470 | line | Get plain string from component |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 476 | line | Trim whitespace and remove formatting codes |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 480 | line | Check if the name contains "Grum" or "Dinnerbone" (case-insensitive) - allows multiple words like "spin Dinnerbone" |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 486 | line | If JSON parsing fails, try checking the raw string |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 487 | line | Sometimes the name might be stored as plain text |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 504 | line | Check NBT directly for display.Name tag |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 510 | line | Check if display compound exists |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 511 | line | 10 = TAG_COMPOUND |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 516 | line | 8 = TAG_STRING |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 520 | line | Get the name JSON string |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 526 | line | Parse the JSON text component to get plain text |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 534 | line | Get plain string from component |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 540 | line | Trim whitespace and remove formatting codes |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 544 | line | Check if the name is exactly "item" (case-insensitive) |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 549 | line | If JSON parsing fails, try checking the raw string |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 602 | line | Renaming 'Fixed' only affects weapons/tools: Sword, Trident, Axe, Pickaxe, Shovel, Hoe |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PillarBlockEntityRenderer.java` | 630 | line | Kingodogo Finished this File on 2025-12-10 20-50-05 |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 23 | block | * Replaces, rather than overlays, the world-water surface beside an outlet. Author: HoYin1600p. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 28 | block | * Direction points from the world-water block toward the pipe. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 57 | line | A downward outlet replaces the first falling block's mesh, joining its neck to the pipe. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 70 | line | Ordinary waterfalls and submerged water do not need a horizontal spill. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 101 | line | Match directed straight pipes where the inlet end is specifically chosen |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 147 | line | NW, SW, SE, NE, independent of winding. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 205 | line | Four skirts join the inner opening to vanilla's existing perimeter. Keeping that perimeter |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 206 | line | unchanged also joins adjacent flowing water without holes or overlapping translucent faces. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeSpillVertexConsumer.java` | 242 | line | Preserve vanilla's two triangles, including UVs; bilinear interpolation would reshape the water. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/PipeWaterSurface.java` | 7 | block | * Shared channel heights for the pipe surface and its outlet spill. Author: HoYin1600p. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ShelfRenderer.java` | 34 | block | * Vanilla ShelfRenderer.ITEM_SIZE. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/ShelfRenderer.java` | 137 | line | A model that refuses to render off-screen is not worth crashing the world over. |
| `src/main/java/com/kingodogo/buildscape/client/renderer/SignFrameRenderer.java` | 22-25 | block | * * Handles rendering cosmetic frames over signs. * Aligns properly with both standing signs (at any rotation) and wall signs (on any wall). |
| `src/main/java/com/kingodogo/buildscape/client/renderer/SignFrameRenderer.java` | 69 | line | Offset from North wall sign board position to standing sign board position |
| `src/main/java/com/kingodogo/buildscape/client/renderer/TrappedDecoratedPotBlockEntityRenderer.java` | 16-20 | block | * * Renderer for {@link TrappedDecoratedPotBlockEntity}. * Identical wobble logic to {@link DecoratedPotBlockEntityRenderer} but * operates on the trapped pot's block entity type. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersPouchScreen.java` | 19 | block | * Artwork size; the sheet itself is 256x256 with the panel anchored at (0,0). |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersPouchScreen.java` | 24 | line | The flat face of the banner, excluding both the dark frame (x54/x133, y0/y16) and |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersPouchScreen.java` | 25 | line | the 1px bevel inside it (x55/x132, y1/y15) - same 76x13 area as the workbench. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersPouchScreen.java` | 35 | line | Both captions are part of the artwork now, so the vanilla labels are pushed |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersPouchScreen.java` | 36 | line | off-screen rather than drawn on top of it. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 24 | line | ── Layout ──────────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 25 | line | All values below are relative to leftPos/topPos and mirror the artwork in |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 26 | line | textures/gui/builders_workbench/{color,gradient}_builder_bg.png (256x256 sheets, |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 27 | line | artwork anchored at 0,0). Slot coordinates MUST stay in sync with |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 28 | line | BuildersWorkbenchMenu - see the LAYOUT block there. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 33 | line | Tab sprites are 17x17 with the icon baked in. TAB_Y = -1 keeps their bottom edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 34 | line | exactly where the artwork expects it (GUI y = 15, one row above the panel top), |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 35 | line | and the 3px gap matches the spacing drawn in the mockup. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 41 | line | Title banner (baked into the background). Every title is scaled to the same |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 42 | line | usable width, so both tabs end up with an identical margin on each side even |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 43 | line | though their strings differ in length. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 44 | line | Banner interior, measured inside the dark frame baked into the background sheet. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 45 | line | The flat face of the banner, excluding both the dark frame (x52/x131, y0/y16) and |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 46 | line | the 1px bevel inside it (x53/x130, y1/y15). Centring on the flat area rather than |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 47 | line | the frame is what makes the margins come out even on both sides. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 53 | line | Filter buttons (18x18, stacked vertically) |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 59 | line | Compact modifier controls occupy the unused lower-left strip of both panels. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 60 | line | Their 11x11 hitboxes do not overlap the workbench slots or the copy arrow. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 65 | line | Copy arrow (48x16, animated) - identical position on both tabs. The sprite has |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 66 | line | transparent padding: the ink sits at x 4..43, y 2..12, symmetric around row 7. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 67 | line | These values put that ink in the middle of the 46px gap between the pouch slots. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 71 | line | Slot interiors used for the re-roll dots (must match the menu) |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 77 | block | * Above the item render layer (items blit around Z 100-200) so the dots stay visible. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 106 | line | AbstractContainerScreen#init recentres on imageWidth, which would undo the |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 107 | line | anchoring above, so re-apply it once the vanilla layout pass is done. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 164-174 | block | * * Sizes the screen for the active tab, but anchors it as if it were always the * colour builder. * * <p>The gradient artwork is wider only because of the filter panel hanging off its * right-hand side - the main body starts at x = 0 in bo... |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 239 | line | The whole static layout - panel, frames, every slot background, the player |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 240 | line | inventory and the idle arrow - lives in a single background texture. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildersWorkbenchScreen.java` | 301-305 | block | * * Drawn after super.render() and lifted on the Z axis: item stacks are rendered at a * blit offset of their own, so without this the dots would disappear under any item * sitting in the slot. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 11 | line | 11% sidebar |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 12 | line | 0.5% gap before sidebar |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 13 | line | 0.5% gap after sidebar |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 14 | line | 0.5% gap between panels |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 15 | line | 0.5% gap after right panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 16 | line | 0.5% gap height |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 18 | line | Panel Widths: (100% - 0.5 - 11 - 0.5 - 0.5 - 0.5) / 2 = (100 - 13.0) / 2 = 87.0 / 2 = 43.5% |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 162 | line | Silently handle - dimensions will be set correctly in init() via Minecraft's normal flow |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 174 | line | Sidebar uses 0.5% padding on its LEFT (screen edge) |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 175 | line | Sidebar Column = 11% of screen width |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 176 | line | Buttons should have 0.5% gap on both sides within this 11% column. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 177 | line | So Button X = Left Gap (0.5%) + Button Left Margin (0.5%)? |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 178 | line | User said: "buttons inside as we decided should leave 0.5% gap on both side of the nav bar" |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 179 | line | This likely means the sidebar background is the 11% column (starting at 0.5% screen X). |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 180 | line | And buttons are inside that with 0.5% padding relative to screen width? |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 181 | line | Or relative to the sidebar itself? "on both side of the nav bar" implies the bar has padding. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 182 | line | Let's interpret: |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 183 | line | Sidebar Area: Starts at 0.5% screen X, Width 11% screen. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 184 | line | Buttons: Start at Sidebar X + 0.5% screen w. Width = Sidebar Width - 1% screen w. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 189 | line | 0.5% margin |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 193 | line | Start at 5% height |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 262 | line | Explicitly re-initialize the active tab to restore custom widgets if swapping back from another Screen (like ConfirmScreen) |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 279 | line | Show message using ClientEvents overlay |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 399 | line | Calculate max text width to ensure all buttons use the same scale |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 409 | line | Padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 417 | line | Apply a 0.90 multiplier as a safety margin to ensure text never touches the edge or triggers truncation |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 421 | line | Apply common scale to all category buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 437 | line | Define gradient colors: Cyan -> Blue -> Purple -> Magenta -> Orange |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 440 | line | Draw border/shadow first |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 449 | line | Standard text drop shadow equivalent |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 452 | line | Draw gradient text character by character |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 459 | line | Calculate the exact exact start x position using substring to preserve font kerning alignments |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 491 | line | Default passthrough without cropping UV bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 523 | line | Top edge - cropped from 178x1 native width |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 525 | line | Bottom edge - cropped from 178x1 native width |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 527 | line | Left edge - cropped from 1x22 native height |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 529 | line | Right edge - cropped from 1x22 native height using the secondary right-edge asset |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 532 | line | Corners (no cropping needed, they are exactly 6x6) |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 567-569 | block | * * Refreshes the currently active tab. Called when data is received from server. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 641 | line | Sidebar background: Draw from StartX to StartX + Width |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 648 | line | Use the native scaling function instead of hardcoded 20 so the frame actively shrinks naturally to match the Category buttons on high UI scales |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 661 | line | Aligning exact center using half frame height and half scaled font height. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 669 | line | Use renderGradientTitle instead of drawCenteredString |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 682 | line | Disable any scissor tests that might clip tooltips |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 684 | line | Render tooltips last so they appear on top of everything |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 757 | line | Content panels MUST start at exactly the same Y as the first sidebar button. |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 758 | line | The title frame sits at scaleSize(10) with frameHeight = getScaledCategoryButtonHeight() + scaleSize(4). |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 759 | line | First sidebar button Y = titleBottom + spacing (scaleSize(8)). |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 787 | line | Total panel height: from content top down to 0.5% from bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 789 | line | 0.5% bottom gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 793-796 | block | * * Returns the consistent 0.5% vertical gap between stacked panels. * Use this everywhere instead of hard-coding (int)(height * 0.005). |
| `src/main/java/com/kingodogo/buildscape/client/screen/BuildScapeConfigScreen.java` | 801-804 | block | * * Returns the single standard text scale for ALL label/header text across every tab. * Keeps all panel text the same visual size regardless of GUI scale. |
| `src/main/java/com/kingodogo/buildscape/client/screen/DebugRenderConfig.java` | 3-6 | block | * * Configuration for debug rendering options in the UI. * Set these flags to enable/disable visual debugging aids. |
| `src/main/java/com/kingodogo/buildscape/client/screen/DebugRenderConfig.java` | 9-13 | block | * * When true, renders 1-pixel grey borders around all panels. * Useful for debugging panel boundaries and scrollbar clipping. * Set to false before shipping to production. |
| `src/main/java/com/kingodogo/buildscape/client/screen/DebugRenderConfig.java` | 16-18 | block | * * Border color for panel boundaries (grey). |
| `src/main/java/com/kingodogo/buildscape/client/screen/DebugRenderConfig.java` | 22 | line | Utility class, prevent instantiation |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 44 | line | Render title |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 47 | line | Render instruction |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 52 | line | Render inventory |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 61 | line | Render hotbar (slots 0-8) |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 68 | line | Render main inventory (slots 9-35) |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 84 | line | Render slot background |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 90 | line | Render item |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 103 | line | Check if clicking on inventory slot |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 111 | line | Check hotbar |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 125 | line | Check main inventory |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 149 | line | Add item through the config tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 152 | line | Fallback: directly call the config |
| `src/main/java/com/kingodogo/buildscape/client/screen/InventoryItemSelectorScreen.java` | 165 | line | ESC |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 40 | line | Panel coordinates |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 55 | line | IMPORTANT: Request fresh pillar data from server when opening the tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 56 | line | This ensures we always have the latest data, especially on multiplayer servers |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 59 | line | Multiplayer - request data from server |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 65 | line | Load pillar data |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 70 | line | Ignore reload errors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 79 | line | Try to sync pattern from block entity |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 89 | line | Back button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 96 | line | Yellow/White on hover |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 99 | line | Pattern selector |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 100 | line | Default to "none" if null (global) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 101 | line | If pattern logic in this tab uses "none" to represent null/global |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 112 | line | Allow component colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 116 | line | Use pattern toggle |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 131 | line | Pattern speed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 148 | line | Pattern spread |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 165 | line | Pattern intensity |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 182 | line | Max particle color slider |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 195 | line | Save button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 204 | line | Color swatches and hex fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 222 | line | Use default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 264 | line | Invalid hex, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 271 | line | Color picker - reduced size to fit in window |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 288 | line | Ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 299 | line | Initial layout |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 307 | line | Use dimensions from parent screen directly to ensure consistency |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 313 | line | Button area at top for back button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 316 | line | Position back button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 322 | line | LEFT PANEL: Settings |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 328 | line | RIGHT PANEL: Colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 334 | line | Left Panel Layout: Config fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 338 | line | Extra space for title |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 340 | line | Calculate label and field widths |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 381 | line | Save button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 389 | line | Right Panel Layout: Colors (2 column layout like PillarParticlesConfigTab) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 396 | line | Column spacing |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 426 | line | Position color picker below swatches |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 434 | line | Limit picker size to reasonable proportions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 494 | line | Use default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 591 | line | Send packet to server to update pillar data |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 606 | line | Also update local manager for single-player |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 622 | line | Find the level for this pillar's dimension |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 642 | line | Find the bottom of the stack |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 650 | line | Update NBT with settings from manager |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 653 | line | Update pattern |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 662 | line | Update pattern speed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 671 | line | Update pattern spread |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 680 | line | Update pattern intensity |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 689 | line | Update max particle color |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 698 | line | Update colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 717 | line | Clear and set colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 740 | line | Found the pillar, done |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 745-748 | block | * * Sets the height of an EditBox via reflection since 1.18.2 EditBox * doesn't have a public setHeight method. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 756 | line | Fallback - ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 835 | line | Check if relayout needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 855 | line | Header info (Pillar ID) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 867 | line | LEFT PANEL: Borders |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 879 | line | Labels for fields in Left Panel - ensure they are drawn ONLY once |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 899 | line | RIGHT PANEL: Borders |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 911 | line | Status text |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 925 | line | Helper to get consistent pattern message styles |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdDetailConfigTab.java` | 974 | line | Try to find item frame entities at this position |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 30 | line | Helper methods to get scaled values |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 74 | line | Controls sit at the top of the content area; actual positions are set during |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 75 | line | render |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 89 | line | Red text for danger |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 96 | line | Lighter red |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 107 | line | Green for apply |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 111 | line | Header Checkbox |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 141 | line | 50% dark inner bg |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 159 | line | IMPORTANT: Request fresh pillar data from server when opening the tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 160 | line | This ensures we always have the latest data, especially on multiplayer servers |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 163 | line | Multiplayer - request data from server |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 168 | line | Single-player - load locally |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 179 | line | Drop unsaved markers when explicitly reloading |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 186 | line | Check and reload if file changed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 191 | line | IMPORTANT: Get snapshot from manager - this should have colors from file |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 194 | line | Apply snapshot to GUI rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 209 | line | Don't stomp on user edits |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 230-233 | block | * * Check if a pillar is within display range of the player. * Returns true if the pillar is in the same dimension and within 64 blocks. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 238 | line | Reuse rows when possible so caret position is preserved |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 243 | line | Show all pillars - no range filtering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 255 | line | Hide rows that disappeared |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 280 | line | maxScroll is updated during render when we know the viewport height; leave |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 281 | line | base value here |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 357 | line | Sync with server |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 368 | line | Reserve space for scrollbar (always visible in layout calculations) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 373 | line | Item column width: Ensure at least 42px for two 16px items + gaps |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 374 | line | We use scaleSize(54) as base but enforce a minimum of 42 units |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 377 | line | Calculate remaining width for percentage-based columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 380 | line | Define column percentages for remaining columns (total must be 100%) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 381 | line | Adjusted percentages to balance the extra space given to the item column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 382 | line | Gap from bounding box |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 383 | line | Gap between columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 384 | line | Checkbox column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 385 | line | ID column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 386 | line | Color swatches column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 387 | line | Dimension column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 388 | line | Coordinates column (reduced slightly) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 390 | line | Total gaps: 5 gaps * 0.5% = 2.5% |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 393 | line | Verify total equals 100% |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 397 | line | Calculate actual widths from percentages using remaining width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 406 | line | Adjust coords to fill any remaining space from rounding |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 422 | line | Offset buttons so they don't sit on the bounding box |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 423 | line | Start x for buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 425 | line | Reload button on the far left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 432 | line | Remove Selected button next to Reload |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 439 | line | Apply/Save button next to Remove |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 445 | line | Remove All button on the far right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 456 | line | Use full width spanning both left and right areas |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 463 | line | Background - low opacity gray (removed green background) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 464 | line | GuiComponent.fill(poseStack, contentX, contentY, contentX + contentWidth, |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 465 | line | contentY + contentHeight, 0xC0256F16); |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 479 | line | Gap is now at index 7 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 481 | line | Add extra spacing between header and content for better visual separation |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 490 | line | Calculate available height for rows (from rowsStartY to bottom of content) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 492 | line | 0.2% of screen height gap between rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 497 | line | Header background - removed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 498 | line | GuiComponent.fill(poseStack, tableX, tableY, tableX + tableWidth, tableY + |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 499 | line | HEADER_HEIGHT, 0xFF5E8C1A); |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 500 | line | Table border removed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 501 | line | drawTableBorder(poseStack, tableX, tableY, tableWidth, tableHeight); |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 504 | line | Calculate actual table width from columns for scrollbar positioning |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 506 | line | Sum first 7 columns (leftMargin through coordsWidth, not gap) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 509 | line | 5 gaps between 6 content columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 511 | line | Enable scissoring to prevent rows from rendering above the header or below |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 512 | line | the visible area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 516 | line | Calculate available height for rows (from rowsStartY to bottom of table) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 517 | line | Use the same calculation as maxScroll to ensure consistency |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 520 | line | Convert to screen coordinates for scissor (Minecraft uses bottom-left origin) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 532 | line | Reset all row bounds before rendering to avoid stale hits in mouseClicked |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 537 | line | Rows - render all visible rows within the scissor area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 539 | line | Bottom of visible area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 540 | line | 0.2% of screen height gap between rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 547 | line | Only skip rows that are completely outside the visible area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 548 | line | Allow rows that are partially visible (even if bottom is cut off, we want to |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 549 | line | see the top) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 552 | line | Skip completely off-screen rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 555 | line | Store row bounds for double-click detection |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 561 | line | Disable scissoring after rendering rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 564 | line | Draw scrollbar if needed - position at the right edge of actual table |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 566 | line | Position at the end of the columns (actualTableWidth) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 576 | line | Draw 1px border around the entire panel (matching other tabs) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 578 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 579 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 580 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 581 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 583 | line | Status text |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 596 | line | Light silver text for table headers instead of pure white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 600 | line | Increased padding for better spacing |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 602 | line | Calculate exact mathematical center for vertical text alignment using standard bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 603 | line | +1 to visually snap 'p' descending letters |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 605 | line | Apply left margin (1% gap from left panel) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 606 | line | leftMargin at index 0 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 608 | line | Checkbox column (No bounding cell rendered, just position the interactive Master Checkbox) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 614 | line | Active rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 615 | line | checkboxWidth at index 1 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 619 | line | Scale text to fit within column width if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 632 | line | idWidth at index 2 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 634 | line | Item column (NEW) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 645 | line | Re-center after scaling |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 653 | line | itemWidth at index 3 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 669 | line | colorsWidth at index 4 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 685 | line | dimensionWidth at index 5 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 687 | line | Coordinates column - draw X, Y, Z separately aligned to their fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 688 | line | (No drawCell bounding box, coordinates contain their own nested black border) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 690 | line | Calculate positions to match the coordinate fields below |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 693 | line | coordsWidth at index 6 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 697 | line | Adjust if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 704 | line | Draw X, Y, Z labels centered in their respective columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 709 | line | Center text horizontally - use SAME headerTextY as other headers |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 714 | line | Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 715 | line | Light Blue/Purple |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 716 | line | Green |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 720 | line | Subtle 1px outline for sleek appeal |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 721 | line | Slightly brighter border for header |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 722 | line | Slightly darker background for rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 724 | line | Fill inner background |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 727 | line | Render 1px outer borders cleanly |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 728 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 729 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 730 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 731 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 738 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 743 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 748 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 753 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 761 | line | Table border - low opacity gray (removed green border) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 782 | line | Use full width spanning both left and right areas (Consistency with render!) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 786 | line | Handle functional row clicks (checkboxes, fields, double-clicks) FIRST |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 793 | line | Stop after first row handles it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 799 | line | Handle scrollbar click AFTER rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 813 | line | Gap at index 7 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 819 | line | Calculate actual table width for scrollbar interaction |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 821 | line | Sum first 7 columns (leftMargin through coordsWidth) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 824 | line | 5 gaps between 6 content columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 837 | line | Pass 0 for content area if we want to disable content-drag-to-scroll when |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 838 | line | rows are present |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 839 | line | This prevents the scrollbar from swallowing clicks meant for buttons above |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 842 | line | Limit drag area to table |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 933 | line | Remove tracked widgets |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 977 | line | Sync removal to server |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1008 | line | Sync removal to server |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1047 | line | Double-click detection |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1051 | line | 300ms window for double-click |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1052 | line | Max pixel distance for double-click |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1054 | line | Row bounds for click detection |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1063 | line | Load items from data |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1066 | line | Create selection checkbox - Clean 1px border design |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1075 | line | 50% dark inner bg |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1095 | line | Remove white border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1096 | line | Make ID field focusable so it can be clicked |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1099 | line | Create color swatches for each color in the pillar (display-only) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1111 | line | Use default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1117 | line | Display-only, no click action |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1127 | line | Dimension is not editable |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1128 | line | Remove white border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1134 | line | Remove white border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1135 | line | Remove white border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1136 | line | Remove white border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1137 | line | Make coordinate fields non-editable |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1142 | line | Set values first |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1147 | line | Color coordinates: X=Red, Y=Blue, Z=Green (set AFTER setting values to ensure |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1148 | line | colors persist) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1149 | line | Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1150 | line | Light Blue/Purple |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1151 | line | Green |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1176 | line | Don't fallback to vanilla item_frame - colored frames should show their actual color |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1188-1192 | block | * * Load colors directly from NBT/block entity for this pillar. * This ensures colors show in GUI even if manager file doesn't have them. * Works on both client and server side. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1200 | line | Check if we're in the right dimension |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1203 | line | Not in the right dimension - try server if available |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1236 | line | Find the bottom of the stack to get the actual block entity with colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1245 | line | Get colors directly from NBT |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1255 | line | We're in the right dimension - use client world |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1267 | line | Find the bottom of the stack to get the actual block entity with colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1275 | line | Get colors directly from NBT |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1281 | line | Silently fail - will fall back to manager data |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1291 | line | Load pillar type icon from manager data |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1308 | line | Load displayed item from data |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1325 | line | IMPORTANT: Use colors from manager file (pillar-ids.dat) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1326 | line | File is the source of truth for GUI display |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1327 | line | Ensure we have a valid list (never null) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1335 | line | Clear all existing swatches first |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1338 | line | Create swatches for all colors - this should create swatches if colors exist |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1347 | line | Use default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1353 | line | Display-only, no click action |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1360 | line | Set coordinate values first |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1364 | line | Ensure coordinate colors are set AFTER setting values to ensure colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1365 | line | persist |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1366 | line | Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1367 | line | Light Blue/Purple |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1368 | line | Green |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1372 | line | Update item stacks |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1400 | line | Deprecated logic. Custom rendering explicitly handles selection states now. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1414 | line | Get colors from swatches - we'll preserve existing colors since swatches are |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1415 | line | read-only display |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1416 | line | Colors are managed in the detail tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1441 | line | Gap is at index 7 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1447 | line | Calculate total row width (for layout purposes only, no border drawn) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1449 | line | Sum leftMargin through coordsWidth |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1452 | line | 5 gaps between 6 content columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1454 | line | Calculate EXACT center Y position for all elements - use ONE calculation for |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1455 | line | EVERYTHING |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1456 | line | This ensures perfect horizontal alignment across all columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1459 | line | SIMPLIFIED CENTER ALIGNMENT: |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1460 | line | Calculate ONE center Y position for the entire row and align ALL elements to |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1461 | line | it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1466 | line | Use the row center as the reference point for ALL elements |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1469 | line | Position checkboxes: vertically centered |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1472 | line | Position color swatches: vertically centered |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1475 | line | Position EditBoxes: vertically centered |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1478 | line | Position text: Mathematically perfectly centered between the cell's top and bottom border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1483 | line | Apply left margin (1% gap from left panel) - matches header |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1484 | line | leftMargin at index 0 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1486 | line | Checkbox column - perfectly centered vertically (No bounding cell rendered) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1488 | line | Use calculated center for checkbox |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1494 | line | ID column - perfectly centered |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1496 | line | Render Pillar ID EditBox background |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1500 | line | Clear text temporarily to draw it directly for alignment |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1505 | line | Draw Pillar ID text with scaling if too long to fit in column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1511 | line | Scale down text to fit |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1515 | line | Adjust position for scaled text |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1525 | line | Item column - items are NEVER scaled, always 16x16, must fit within column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1528 | line | Items are always rendered at their native 16x16 size |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1534 | line | Use gaps between items |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1535 | line | Gap between items |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1537 | line | Calculate total width needed for items (no edge gaps, will center instead) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1541 | line | Center items within column, but shift left by 1px to add more space on right side |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1545 | line | Ensure we don't go negative or too far right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1554 | line | Add item gap only if there's a second item |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1565 | line | Colors column - swatches must fit within column bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1567 | line | Render color swatches - scale to fit and left-align with padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1573 | line | Calculate maximum swatch size that fits within available width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1575 | line | Clamp to minimum size and rowHeight |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1578 | line | Ensure swatches actually fit |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1581 | line | Recalculate to definitely fit |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1585 | line | Left-align with padding to ensure they stay within column |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1587 | line | Center vertically |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1601 | line | Dimension column - perfectly centered |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1603 | line | Format dimension name for display (remove modid prefix like "minecraft:" and show full name) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1607 | line | Render dimension EditBox background (no text) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1609 | line | Use EXACT same centerY as all other EditBoxes |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1613 | line | Temporarily clear text to draw formatted version |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1619 | line | Draw formatted dimension text - scale down if needed to fit |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1625 | line | Scale down text to fit |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1636 | line | Coordinates column - perfectly centered with border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1637 | line | (No drawCell bounding box, coordinates contain their own nested black border) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1639 | line | Improved coordinate alignment - ensure consistent spacing with proper scaling |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1644 | line | Ensure minimum width per coordinate field (reduced since text is smaller) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1648 | line | If total width exceeds available space, reduce gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1655 | line | Calculate positions for coordinate fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1660 | line | Draw border around all coordinate fields as one component - BLACK border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1661 | line | Black border for coordinates |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1662 | line | Ensure at least 1 pixel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1663 | line | Space between border and fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1669 | line | Draw outer border rectangle - ensure no overlap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1670 | line | Top border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1673 | line | Bottom border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1676 | line | Left border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1679 | line | Right border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1683 | line | Vertical dividers between fields - positioned to not overlap with outer |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1684 | line | border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1685 | line | Divider 1: between X and Y fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1687 | line | Divider 2: between Y and Z fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1690 | line | Draw dividers from inner border to inner border (not overlapping outer |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1691 | line | border) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1699 | line | Ensure all coordinates are perfectly aligned - centered vertically |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1712 | line | Draw coordinate text directly with colors using SAME textY as all other text |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1713 | line | Apply a smaller base scale to coordinate text to make it more compact |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1714 | line | Center text horizontally within each coordinate field and scale if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1719 | line | Make coordinates smaller (75% of original size) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1722 | line | X coordinate (Red) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1740 | line | Y coordinate (Light Blue/Purple) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1758 | line | Z coordinate (Green) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1777-1785 | block | * * Format dimension name for display by removing modid prefix and capitalizing * properly. * Examples: * - "minecraft:overworld" -> "Overworld" * - "minecraft:the_nether" -> "The Nether" * - "minecraft:the_end" -> "The End" * - "overworld"... |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1788 | line | Default |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1793 | line | Remove modid prefix (e.g., "minecraft:" or any other modid) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1801 | line | Handle underscores and capitalize properly |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1802 | line | Replace underscores with spaces |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1805 | line | Capitalize first letter of each word |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1845 | line | First check if mouse is over row vertically and within content area (exclude scrollbar) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1850 | line | Calculate actual content width (exclude scrollbar area) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1855 | line | Ignore clicks on scrollbar area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1860 | line | Check if clicking on checkbox - ONLY the checkbox toggles selection |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1865 | line | Check if clicking on the ID field area (single click opens detail tab) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1867 | line | Check if mouse is over ID field bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1870 | line | Single click on ID field opens detail tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1876 | line | Check if clicking on interactive fields (swatches, dimension, coords) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1877 | line | These handle their own click logic but do NOT toggle selection |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1888 | line | Consumed by field, no selection toggle |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1891 | line | Get column positions to determine which column was clicked |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1893 | line | Gap at index 7 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1895 | line | Calculate column X positions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1896 | line | leftMargin + checkbox |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1908 | line | Only handle double-click if clicking on specific columns (ID, Item, Color, Dimension, Coords) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1917 | line | Ignore clicks on checkbox column or gaps |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1920 | line | Check if it's in the ID column area - single click opens detail tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1926 | line | Double-click on valid columns for marking pillar in world |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1947 | line | Consumed the row click |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1963 | line | Get pillar data |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1972 | line | Check if we're in the same dimension |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1975 | line | Can't mark pillar in different dimension |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1979 | line | Mark the pillar (this will render a bounding box for the whole stack) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1982 | line | Calculate direction to face the pillar |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1987 | line | Set player rotation to face the pillar |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 1993 | line | Close the GUI |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2014-2017 | block | * * Spawns a particle effect around the pillar to mark it visually. * Creates a glowing box effect for 2 seconds. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2028 | line | Spawn particles in a box pattern around the block |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2029 | line | This creates a visible bounding box effect |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2031 | line | Bottom face corners |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2039 | line | Top face corners |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2047 | line | Vertical edges - front |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2054 | line | Vertical edges - back |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2061 | line | Horizontal edges - bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2071 | line | Horizontal edges - top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2081 | line | Add some enchant particles for extra glow |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2094 | line | Default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2098 | line | Green for overworld |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2100 | line | Red for nether |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2102 | line | Purple for end |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2106 | line | Draw a simple colored square as the icon |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2108 | line | Draw a border |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2109 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2110 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2111 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarIdsConfigTab.java` | 2112 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 44 | line | Get screen dimensions for percentage-based calculations |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 48 | line | Calculate content area using percentage-based system |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 54 | line | Layout: 11% sidebar + 44% left content + 1% gap + 44% right content (all from |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 55 | line | full screen width) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 56 | line | Each section takes 50% of content height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 63 | line | Vertical Layout: |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 64 | line | Top Gap: 5% (handled by parent.getContentY()) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 65 | line | Available Height = Screen Height - Top Gap - Bottom Gap (0.5%) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 66 | line | We have two panels separated by a middle gap (0.5%) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 67 | line | Panel Height = (Available Height - Middle Gap) / 2 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 71 | line | 0.5% consistent gap between panels |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 76 | line | Calculate positions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 81 | line | 0.5% internal padding for buttons relative to panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 84 | line | Top-Left: Selected items (44% of full screen width, 50% height) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 97 | line | Dynamically calculate header height based on label position/height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 103 | line | Bottom-Left: Item selector panel (44% width, 50% height) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 104 | line | Create a container "panel" widget to hold all components together |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 107 | line | Create a dummy container widget to represent the panel bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 108 | line | This ensures all child components scale together |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 109 | line | Small top padding to prevent clipping |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 110 | line | Space for 3 buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 114 | line | Create panel container (invisible, just for positioning reference) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 121 | line | Invisible container |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 130 | line | Calculate button group dimensions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 134 | line | Buttons end flush at panel right edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 138 | line | Start slightly down from panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 140 | line | Create toggle buttons - Right aligned |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 176 | line | Search box - positioned after label text, extending to buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 177 | line | Label |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 185 | line | Search box ends before buttons with some spacing |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 191 | line | Align Y with buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 192 | line | Match height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 202 | line | Create item selection widget - starts at panel top to cover entire area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 203 | line | Widget will internally handle spacing for search box via HEADER_AREA_HEIGHT |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 205 | line | Start at panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 213 | line | Dynamically calculate header height based on search box position/height to prevent overlap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 218 | line | Top-Right: Presets (44% of full screen width, 50% height) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 230 | line | Get create button from presets widget for GUI config |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 232 | line | Match PresetsWidget calculation: x + scaledSpacing, y + height - |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 233 | line | scaleSize(35) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 238 | line | Bottom-Right: Tag selector panel (44% width, 50% height) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 239 | line | Create panel container for tags section |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 250 | line | Invisible container |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 259 | line | Calculate width for tags buttons (same size/spacing) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 260 | line | Re-calculate these local variables to be safe in case code above changes |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 265 | line | Buttons end flush at panel right edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 270 | line | Create tags sort buttons - Right aligned |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 305 | line | Tags Search box |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 316 | line | Match buttons Y |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 317 | line | Match buttons height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 327 | line | Create tags selector widget - starts at panel top to cover entire area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 328 | line | Widget will internally handle spacing for search box via HEADER_AREA_HEIGHT |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 329 | line | Full panel height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 332 | line | Start at panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 337 | line | Match tag selector header height for consistency |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 341 | line | Initialize widget connections |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 349 | line | Update child component positions relative to their parent widgets |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 352 | line | Update selected tags from config |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 355 | line | Auto-apply preset on init (unnamed or last applied) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 360 | line | Select the preset that was applied |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 369-373 | block | * * Update positions of child components (search boxes, buttons) relative to * their panel bounds. * This ensures components stay aligned when panels resize. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 375 | line | Get current panel bounds using parent helper methods for consistency |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 386 | line | 0.5% consistent gap between panels |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 397 | line | Position search box Y coordinate |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 403 | line | Position buttons Y coordinate |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 411 | line | Update item selection widget position - starts at panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 414 | line | Start at panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 421 | line | Full panel height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 433 | line | Ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 438 | line | Update tags panel components (bottom-right) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 452 | line | Update tags selector widget position - starts at panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 454 | line | Full panel height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 456 | line | Start at panel top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 465 | line | Update PresetsWidget internal button positions (including Create button) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 470 | line | Update search box position/width based on current label text |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 482 | line | Clear unnamed preset when a preset is applied |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 491 | line | Tag is already selected, remove it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 494 | line | Tag is not selected, add it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 497 | line | Save changes to unnamed preset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 525 | line | Start with buildscape if available |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 532 | line | Deselect all buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 537 | line | Select the clicked button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 547 | line | Cycle to next/prev mod if clicking again |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 556 | line | Set to first mod or buildscape if available |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 576 | line | Update search box position/width when label changes |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 580-584 | block | * * Updates the search box position and width based on the current label text. * This ensures the search box auto-resizes when the label changes (e.g., "All * items" -> "Inventory Items" -> "Mod Items"). |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 595 | line | Get current label text based on sort mode |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 624 | line | Dynamically compute maxLabelWidth to ensure searchBox is minimum 80px width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 630 | line | Buttons end flush at panel right edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 640 | line | Update button positions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 688 | line | Buttons end at panel right edge (no extra right gap inside panel) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 714 | line | Deselect all tags buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 719 | line | Select the clicked button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 732 | line | Apply sort mode to tags selector widget |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 760 | line | Update search box position/width when mod changes (label text changes) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 772 | line | Update search box position/width when mod changes (label text changes) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 787 | line | Only toggle if explicitly clicked - don't auto-remove on load |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 791 | line | Item is already in config, remove it (user clicked to remove) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 793 | line | Save changes to unnamed preset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 800 | line | Item is not in config, add it (user clicked to add) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 802 | line | Save changes to unnamed preset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 812 | line | Save current items to unnamed preset (unsaved changes) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 816 | line | Don't save if default preset is selected and no changes were made |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 820 | line | If a named preset is selected, switch to unnamed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 823 | line | If default is selected, switch to unnamed to track changes |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 843 | line | Save changes to unnamed preset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 858 | line | Calculate quadrant boundaries |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 859 | line | Match spacing from init() |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 866 | line | Render existing items widget (top-left quadrant) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 871 | line | Render "Pillar items" label inside existingItemsWidget area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 872 | line | Render AFTER widget to ensure it's visible on top with background for |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 873 | line | visibility |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 876 | line | Bring label to front with highest z-level |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 887 | line | Render with white color and scaling for high GUI scales (no background |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 888 | line | needed) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 896 | line | Full opacity white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 902 | line | Render labels aligned with search boxes on the same line |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 904 | line | Bring labels to front with higher z-level |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 910 | line | Render Search Box Label |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 941 | line | Render "Tags" label |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 962 | line | Render search box and toggle buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 976 | line | Render item selection widget |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 981 | line | Render presets widget (top-right) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 986 | line | Render tags search box (bottom-right, above tags selector) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 991 | line | Render tags sort buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1002 | line | Render tags selector widget (bottom-right) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1010 | line | Render tooltips for sort buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1019 | line | Render tooltips for item selection widget |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1024 | line | Render tooltips for existing items widget |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1093 | line | Forward to all widgets for scrollbar dragging |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1108 | line | Forward to all widgets for scrollbar release |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1145 | line | Clear search boxes when leaving the tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1153 | line | Clear tags search box when leaving the tab |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1161 | line | Refresh items when tab is closed to ensure latest state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarItemsConfigTab.java` | 1163 | line | Remove tracked widgets |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 27 | line | Consolidated constants for consistent layout |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 33 | line | Increased for better clarity |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 34 | line | More gap between button and fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 39 | line | Spacing for Color Swatches |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 40 | line | Small and neat |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 41 | line | Matching user's request for gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 43 | line | 5px top + ~9px font + 2px safety gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 55 | line | Single color picker widget (only one visible at a time) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 56 | line | 7 color swatch buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 57 | line | Hex code edit boxes next to color swatches |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 59 | line | Always 7 swatches |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 60 | line | Which color swatch is currently selected (-1 = none) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 61 | line | Track which picker is being dragged |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 62 | line | Track if slider is being dragged |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 75 | line | Blue |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 77 | line | Green |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 79 | line | Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 86 | line | Blue |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 88 | line | Default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 92 | line | Cyan |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 95 | line | Magenta |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 98 | line | Green |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 101 | line | Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 104 | line | Gold |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 107 | line | Light Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 110 | line | Light Cyan |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 116 | line | Translate pattern name properly if localized, or just capitalize |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 136 | line | Load current values |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 140 | line | Widgets are created once; layout applied via relayout() |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 141 | line | Widgets are created once; layout applied via relayout() |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 147 | line | Green text for "Use Pattern true" (cool looking) - handled by TextComponent colors now |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 148 | line | Ensure no override so component colors show |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 218 | line | Color swatches and single shared color picker |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 221 | line | Will be created in relayout |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 228 | line | Allow component colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 293 | line | Ensure server is synchronized with the new global defaults |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 307 | line | Disable if use_pattern is false |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 310 | line | Initial layout - this will create color swatches and shared picker |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 313 | line | Update swatches enabled state based on max value |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 316 | line | Update last dimensions to prevent immediate relayout |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 336 | line | Refresh UI state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 367 | line | Update local state and widgets |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 372 | line | Re-create/Update swatches |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 374 | line | Ensure positions are updated after re-creation |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 375 | line | Ensure enabled state is correct |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 386 | line | Clear existing widgets |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 394 | line | Reinitialize lists if null |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 402 | line | Ensure config has 7 colors |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 407 | line | Create 7 color swatches with hex fields in right top panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 408 | line | Below title |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 421 | line | Use default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 426 | line | Create color swatch button |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 435 | line | Create hex field next to swatch (side by side, same Y position) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 436 | line | Align hex field vertically with swatch (center it if heights differ) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 437 | line | Same Y position for side-by-side alignment |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 439 | line | Center vertically if heights differ |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 451 | line | #RRGGBB |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 453 | line | Update color when hex is edited |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 455 | line | Handle hex with or without # prefix |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 461 | line | Only process if we have a valid hex color (6 hex digits after #) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 465 | line | Prevent feedback loop: don't update picker if the change came FROM the picker |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 471 | line | Update swatch button color visually |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 474 | line | Update hex field value to ensure it has # prefix |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 480 | line | Invalid hex, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 487 | line | Create shared color picker (initially hidden, shown when swatch is clicked) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 488 | line | Size will be recalculated during render, but set initial size for RGB/HSB |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 489 | line | sliders |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 491 | line | Moved down by 3 pixels as per request |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 493 | line | Width needed for gradient + hue + RGB/HSB sliders |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 494 | line | Height needed for gradient + preview + RGB/HSB sliders |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 503 | line | Update hex field |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 507 | line | Update swatch button color |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 514 | line | Ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 519 | line | Initially hidden |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 524 | line | Only allow clicking if swatch is enabled (within max range and use_pattern is |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 525 | line | true) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 527 | line | Swatch is locked, don't allow clicking |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 532 | line | Pattern mode not enabled |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 537 | line | Get current color for this index |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 545 | line | Use default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 548 | line | Update shared color picker with this color and show it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 557 | line | Update swatch button color |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 563 | line | Base positions for color swatches (without scroll offset) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 566-569 | block | * * Sets the height of an EditBox via reflection since 1.18.2 EditBox * doesn't have a public setHeight method. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 577 | line | Fallback - ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 582 | line | Space for reset button + Swatches in 2 columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 584 | line | 4 rows |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 596 | line | More space between columns |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 625 | line | Box positions and sizes (stored for consistent rendering) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 626 | line | Middle panel (44% width): Top 50% (Default Properties), Bottom 50% (Pattern |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 627 | line | Properties) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 630 | line | Right panel (44% width): Top 50% (Color Swatches), Bottom 50% (Color Selector |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 631 | line | and Max Particles) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 634 | line | Track last layout dimensions to avoid unnecessary relayouts |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 638 | line | Scrolling for panels |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 644 | line | Base positions for default properties (without scroll offset) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 648 | line | Base positions for pattern properties (without scroll offset) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 652 | line | Update widget positions with scroll offset applied |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 669 | line | Space for header clip + button + gap + fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 674 | line | Space for header clip + button + gap + 4 items (Slider + 3 Fields) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 678 | line | Update pattern properties widget positions with scroll offset applied |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 702-709 | block | * * Recomputes positions/sizes for all widgets based on current content area and * GUI scale. * Layout: 11% sidebar + 44% middle + 1% gap + 44% right (all from full screen * width) * Middle panel: Top 50% (Default Properties), Bottom 50% (P... |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 711 | line | Internal padding within boxes |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 714 | line | 0.5% consistent gap between panels |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 717 | line | Split for left side (Two panels with middle gap) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 722 | line | Middle panel - Top 50%: Default Properties |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 728 | line | Middle panel - Bottom 50%: Pattern Properties |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 732 | line | Ensure bottoms perfectly align flush |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 734 | line | Right panel - Matches combined height of both middle panels + gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 742 | line | Right panel - Bottom section no longer used (color picker is now in top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 743 | line | panel) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 745 | line | Layout Middle Top: Default Properties (within defaultBox bounds) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 746 | line | Calculate all positions dynamically based on panel dimensions to ensure |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 747 | line | everything fits |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 749 | line | Decreased to make value boxes wider |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 750 | line | Start fields earlier (overlap slightly with label end for tighter |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 751 | line | layout) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 753 | line | Calculate vertical layout - use fixed spacing, enable scrolling if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 756 | line | Space for "Default Properties" title |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 758 | line | Particle Speed, Spread, Lifetime, Density |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 761 | line | Calculate total content height needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 765 | line | Always reserve space for scrollbar to prevent fields from overlapping it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 768 | line | Calculate end position: if scrollbar exists, end before scrollbar with |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 769 | line | offset, otherwise use full width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 772 | line | Components end before the scrollbar with offset (scrollbar starts at panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 773 | line | edge - scrollbarWidth) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 776 | line | No scrollbar, use full width minus padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 780 | line | CRITICAL: Do NOT override componentEndX - it must respect scrollbar position! |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 781 | line | The fields will be narrower if needed, but they MUST end before the scrollbar |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 783 | line | Position button - extend from label start to component end |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 785 | line | Calculate button width - button ends exactly at componentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 788 | line | Minimum button width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 790 | line | Calculate field width - fields MUST end exactly where button ends (at |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 791 | line | componentEndX) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 792 | line | Button ends at: buttonStartX + buttonWidth = componentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 793 | line | Fields should end at: componentEndX (same as button) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 795 | line | CRITICAL: Ensure fieldWidth never exceeds what it should be |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 798 | line | Ensure field + width never exceeds componentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 805 | line | Final verification: both button and fields end at componentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 806 | line | Button end: buttonStartX + buttonWidth = componentEndX ✓ |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 807 | line | Field end: fieldX + fieldWidth = componentEndX ✓ |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 809 | line | Store base positions (without scroll offset) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 810 | line | Position buttons exactly at the header clip for a tighter look |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 814 | line | Set widget X positions and widths (these don't change with scrolling) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 815 | line | FINAL SAFETY CHECK: Ensure fieldWidth never exceeds componentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 835 | line | Update Y positions with scroll offset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 838 | line | Layout Right Top: Color Swatches and Shared Picker |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 839 | line | Always update positions to ensure they stay within panel bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 843 | line | Always reposition to ensure they scale with panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 846 | line | Layout Middle Bottom: Pattern Properties (within patternBox bounds) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 848 | line | Decreased to make value boxes wider |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 849 | line | Use a dynamic gap based on screen height to separate text and values (requested 0.2%) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 853 | line | Define pattern properties constants first |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 854 | line | Reduced spacing between fields (matching user's changes) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 857 | line | Reduced spacing between button and first field (matching user's changes) + dynamic gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 859 | line | Calculate field width based on available space in panel - ensure it doesn't |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 860 | line | exceed panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 861 | line | Always reserve space for scrollbar to prevent fields from overlapping it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 862 | line | 8px width + 5px padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 863 | line | Increased Gap between components and scrollbar (10 pixels) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 865 | line | Calculate if scrollbar is needed for pattern properties |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 870 | line | Calculate end position: if scrollbar exists, end before scrollbar with |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 871 | line | offset, otherwise use full width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 874 | line | Components end before the scrollbar with offset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 877 | line | No scrollbar, use full width minus padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 881 | line | Pattern selector button should start at label start and end at component end |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 883 | line | Calculate button width - button ends exactly at patternComponentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 886 | line | Minimum button width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 888 | line | Pattern Properties - position button exactly at header clip |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 892 | line | Calculate field width - fields MUST end exactly where button ends (at |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 893 | line | patternComponentEndX) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 894 | line | Button ends at: patternButtonStartX + patternButtonWidth = |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 895 | line | patternComponentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 896 | line | Fields should end at: patternComponentEndX (same as button) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 898 | line | CRITICAL: Ensure patternFieldWidth never exceeds what it should be |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 901 | line | Ensure field + width never exceeds patternComponentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 908 | line | Final verification: both button and fields end at patternComponentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 909 | line | Button end: patternButtonStartX + patternButtonWidth = patternComponentEndX ✓ |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 910 | line | Field end: patternFieldX + patternFieldWidth = patternComponentEndX ✓ |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 915 | line | FINAL SAFETY CHECK: Ensure patternFieldWidth never exceeds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 916 | line | patternComponentEndX |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 921 | line | Order: Pattern Selector, Max Particles (second), Pattern Speed, Pattern |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 922 | line | Spread, Pattern Intensity |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 923 | line | Positions are updated in updatePatternPropertiesPositions below |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 936 | line | Calculate total content height for pattern box to determine if scrolling is |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 937 | line | needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 938 | line | Note: patternTotalContentHeight and patternAvailableHeight are already |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 939 | line | calculated above in relayout |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 940 | line | Recalculate with slider included for render method |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 942 | line | Start of pattern properties position update - already set above |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 945 | line | Layout Right Panel: Color Selector (sharedColorPicker) - now in top right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 946 | line | panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 947 | line | Position shared color picker in the colorBox (top right panel, now full |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 948 | line | height) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 949 | line | Always position it, even if not visible, so it's ready when a swatch is |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 950 | line | clicked |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 954 | line | Position picker to the right of swatches, below them |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 955 | line | 7 swatches with rowSpacing of 4, plus padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 956 | line | Right side of panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 957 | line | Below swatches |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 959 | line | Ensure picker doesn't overflow panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 970 | line | Don't set visible here - it's controlled by onColorSwatchClicked |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 976 | line | Ensure list is large enough |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 983 | line | Sync color changes to server |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 993 | line | Try field access first, widespread in 1.16-1.18 |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 994 | line | If it's 1.19+, it might be renderDistance().get(). But usually 'renderDistance' works or we can guess 32. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 995 | line | Safe fallback: |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1006 | line | Important: re-sync from manager so that the newly 'locked' |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1007 | line | patterns (for customized pillars) are picked up by the BE immediately. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1031 | line | Update UI - show "Use Pattern True" or "Use Pattern False" with correct styling |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1042 | line | Disable/enable color swatches and max particle color slider based on |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1043 | line | use_pattern |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1048 | line | Update swatches enabled state (considers both use_pattern and max value) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1064 | line | Capture old pattern for transition |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1066 | line | Instantly enable use_pattern to make sure the cycle takes effect visually |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1069 | line | Transition: On the client, proactively lock patterns for any customized pillars |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1070 | line | so the UI feedback is instant and doesn't flicker to the global pattern. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1074 | line | Robust check for modification: has colors or has hardcoded pattern settings |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1079 | line | Lock to the pattern it was using BEFORE the change |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1088 | line | Update Use Pattern display to reflect it was forced on |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1093 | line | Use helper method to get styled message |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1109 | line | Enable/disable swatches based on max value |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1110 | line | Only swatches up to the max value should be clickable |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1113 | line | If currently selected swatch is beyond max, deselect it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1128 | line | Swatch is enabled only if: |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1129 | line | 1. use_pattern is true (pattern mode enabled) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1130 | line | 2. Index is less than currentMaxColor (within allowed range) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1136 | line | Also update hex fields - they should be editable when not locked by max |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1137 | line | particles |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1140 | line | Hex field is editable if: |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1141 | line | 1. use_pattern is true (pattern mode enabled) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1142 | line | 2. Index is less than currentMaxColor (within allowed range) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1165 | line | Always check if relayout is needed (dimensions or screen size changed) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1180 | line | Overall background - removed colorful background |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1181 | line | GuiComponent.fill(poseStack, contentX, contentY, contentX + contentWidth, |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1182 | line | contentY + contentHeight, 0xC0220B0B); |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1184 | line | Header title removed as requested |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1189 | line | Internal padding within boxes |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1191 | line | Middle Top: Default Properties - Render with scissor test to clip to panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1192 | line | bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1193 | line | Scissor coordinates need to account for GUI scale (window pixels, not GUI |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1194 | line | pixels) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1195 | line | Scissor uses window coordinates: X from left, Y from bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1199 | line | Draw border for Default Properties panel (Always render) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1202 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1204 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1206 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1208 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1210 | line | Calculate a bottom offset to prevent content from touching the border (user |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1211 | line | requested ~1%) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1215 | line | Center inside top border padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1219 | line | Raise the bottom of the scissor box by bottomOffset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1222 | line | Clip the top area (header) to prevent scrolling overlap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1227 | line | Render labels and fields for default properties - use actual widget positions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1228 | line | (already have scroll offset applied) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1232 | line | Calculate total content height and scroll range |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1238 | line | Define header area - nothing should render above this |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1243 | line | Sync component visibility - use actual FIELD_HEIGHT for accurate clipping |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1253 | line | Render labels and widgets aligned correctly - widgets must be rendered here |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1254 | line | within scissor test |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1255 | line | Only render labels if they're below the header |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1296 | line | Permanently hide widgets from parent - we render them manually here with |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1297 | line | scissor |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1304 | line | Scissor region variables already defined above for visibility checks |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1306 | line | Hide standard button rendering by not calling super.render or manual fills |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1307 | line | But we DO need to handle tooltips if we had them. Here we just draw the text. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1309 | line | Render button only if below header |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1310 | line | Render button only if some part of it is in visible area |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1312 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1314 | line | Hide again to prevent parent from rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1316 | line | Render text fields only if their row is visible |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1321 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1326 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1331 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1336 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1341 | line | Render scrollbar if needed (before disabling scissor so it gets clipped) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1357 | line | Right Top: Color Swatches and Shared Picker - Render with scissor test to |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1358 | line | clip to panel bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1359 | line | Draw border for Color Swatches panel (debug mode) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1360 | line | Right Top: Color Swatches and Shared Picker - Render with scissor test to |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1361 | line | clip to panel bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1362 | line | Draw border for Color Swatches panel (debug mode) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1363 | line | Debug border removed as permanent border is drawn below |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1368 | line | Clip top area for header |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1370 | line | Sync border logic with middle panels: draw inside the box dimensions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1372 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1373 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1374 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1375 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1379 | line | Debug: Draw panel background to verify panel is visible |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1380 | line | GuiComponent.fill(poseStack, colorBoxX, colorBoxY, colorBoxX + colorBoxWidth, colorBoxY + colorBoxHeight, |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1381 | line | 0x40000000); |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1383 | line | Removed "Custom Properties" title text as requested |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1385 | line | Ensure swatches are created if they don't exist |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1386 | line | Ensure swatches are created if they don't exist |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1392 | line | Update color swatches with current colors and selection state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1393 | line | Update color swatches with current colors and selection state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1396 | line | Update enabled state first (based on max value) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1399 | line | Calculate scroll info for color swatches (2 columns layout) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1405 | line | Render colors reset button (moved to after scissor) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1406 | line | For click handling |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1408 | line | Ensure positions are updated (this is critical - must be called after |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1409 | line | colorBox coordinates are set) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1410 | line | Color positions now include header offset, managed in |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1411 | line | updateColorSwatchesPositions |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1414 | line | Permanently hide widgets from parent - we render them manually here with |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1415 | line | scissor |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1423 | line | Render swatches and hex fields - always render them, scissor test will clip |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1424 | line | them |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1428 | line | Get color for this swatch |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1436 | line | Use default white |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1439 | line | Update swatch button color and selection state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1443 | line | Always render swatches - scissor test will clip them to panel bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1448 | line | Always render hex fields - scissor test will clip them to panel bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1454 | line | Render scrollbar if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1456 | line | 5px |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1457 | line | from |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1458 | line | edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1469 | line | Render shared color picker if a swatch is selected - hide from parent and |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1470 | line | render manually |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1471 | line | Render shared color picker if a swatch is selected - hide from parent and |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1472 | line | render manually |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1476 | line | Render colors reset button AFTER scissor (Text style) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1477 | line | Render colors reset button AFTER scissor |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1491 | line | Remove old manual rendering code |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1493 | line | Balance the popPose call that follows for "Middle Bottom" comment separation if needed, or remove completely. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1494 | line | Actually, just let the flow continue. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1497 | line | Middle Bottom: Pattern Properties - Render with scissor test to clip to panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1498 | line | bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1499 | line | Draw border for Pattern Properties panel (debug mode) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1500 | line | Middle Bottom: Pattern Properties - Render with scissor test to clip to panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1501 | line | bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1502 | line | Draw border for Pattern Properties panel (debug mode) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1503 | line | Draw border for Pattern Properties panel (Always render) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1506 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1508 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1510 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1512 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1515 | line | Raise the bottom of the scissor box by bottomOffset |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1529 | line | Calculate scroll info |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1535 | line | Define header area - nothing should render above this |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1540 | line | Pattern selector button label - only render if below header |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1541 | line | Use actual widget position which already has scroll offset applied |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1542 | line | Don't render text behind the button - the button itself will display the |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1543 | line | pattern name |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1544 | line | The button text is handled by the button's render method, so we don't need to |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1545 | line | render it here |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1547 | line | Sync component visibility - use actual FIELD_HEIGHT for accurate clipping |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1548 | line | Row 1: Pattern Selector |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1552 | line | Row 2: Max Particles (Label + Slider) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1553 | line | Use the slider's Y as reference for the whole row |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1557 | line | Row 3: Pattern Speed (Label + Field) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1561 | line | Row 4: Pattern Spread (Label + Field) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1565 | line | Row 5: Pattern Intensity (Label + Field) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1569 | line | Max Particles label |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1578 | line | Pattern Speed label |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1589 | line | Pattern Spread label |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1600 | line | Pattern Intensity label |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1611 | line | Permanently hide widgets from parent - we render them manually here with |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1612 | line | scissor |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1619 | line | Render widgets for pattern properties - render within scissor test |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1620 | line | only if their row is marked visible |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1622 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1624 | line | Hide again to prevent parent from rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1627 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1629 | line | Hide again to prevent parent from rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1634 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1639 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1644 | line | Make visible for rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1649 | line | Render scrollbar if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1665 | line | Render Panel Titles AFTER all scissors are disabled to ensure they are visible |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1668 | line | Default Properties Title |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1675 | line | Pattern Properties Title |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1682 | line | Color Swatches Title (Optional, check if needed. Keeping it clean as requested before) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1683 | line | If the user wants it, I can add it here. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1685 | line | Update config from current fields |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1692 | line | Always hide from parent to prevent duplicate rendering |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1695 | line | Render manually if a swatch is selected - always render when |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1696 | line | selectedColorIndex is valid |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1698 | line | Recalculate position during render to ensure it's correct |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1703 | line | 4 rows (3 full rows + 1 with 1 swatch) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1704 | line | Swatch area height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1707 | line | Calculate available space for picker |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1708 | line | Start below swatches |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1709 | line | Remaining height |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1710 | line | Available width |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1712 | line | Ideal picker size |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1716 | line | Calculate actual picker size - shrink to fit if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1720 | line | If picker is smaller than ideal, it will scale internally |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1721 | line | Position picker below the swatches, centered horizontally |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1722 | line | Center horizontally |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1723 | line | Moved down by an additional 15 pixels as per request (was 20 offset in availableY) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1724 | line | Below swatches with extra gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1726 | line | Ensure picker doesn't overflow panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1735 | line | Set picker size and position |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1741 | line | Always render the picker - no scissor test here so it floats on top |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1752 | block | * Minimum allowed value for all numeric double/float config fields to prevent crashes. |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1759 | line | Update default properties |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1768 | line | Invalid value, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1778 | line | Invalid value, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1788 | line | Invalid value, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1798 | line | Invalid value, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1802 | line | Update pattern properties |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1811 | line | Invalid value, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1821 | line | Invalid value, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1831 | line | Invalid value, ignore |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1838 | line | Sync field changes to server (speed, spread, intensity, etc.) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1853 | line | Handle scrollbar clicks for Default Properties panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1855 | line | Handle Default Properties scrollbar dragging |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1866 | line | Pass content bounds as defaultBox for drag-to-scroll support |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1879 | line | Handle Pattern Properties scrollbar dragging |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1904 | line | Handle scrollbar clicks for Color Swatches panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1933 | line | Handle color swatch button clicks - temporarily make visible for mouse event |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1948 | line | Handle hex field clicks |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1952 | line | Clear any active dragging |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1958 | line | Handle shared color picker clicks (for dragging) - call directly like legacy |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1959 | line | version |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1960 | line | NO selectedColorIndex check - just call it if it exists |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1962 | line | Make visible temporarily for event handling |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1967 | line | Track which picker started dragging - CRITICAL for dragging |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1968 | line | to work |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1974 | line | Handle usePatternToggle button clicks - temporarily make visible for mouse |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1975 | line | event |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1987 | line | Handle patternSelector button clicks - temporarily make visible for mouse |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 1988 | line | event |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2000 | line | Handle edit box clicks - temporarily make visible for mouse event |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2072 | line | Handle slider clicks - temporarily make visible for mouse event |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2074 | line | Check if mouse is over slider bounds manually (since widget might be hidden) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2091 | line | If we get here, we clicked somewhere that doesn't handle it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2092 | line | Clear any active dragging state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2099 | line | PRIORITY: Handle shared color picker dragging FIRST - call directly like |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2100 | line | legacy version |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2101 | line | NO visibility checks, NO selectedColorIndex checks - just call it if it |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2102 | line | exists |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2103 | line | The picker itself will check if it's being dragged and return true/false |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2104 | line | CRITICAL: Always call mouseDragged if picker exists - it will handle its own |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2105 | line | state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2107 | line | Make visible temporarily for event handling |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2110 | line | ALWAYS call mouseDragged - it will return true if dragging, false otherwise |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2114 | line | Track for reference |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2115 | line | Return immediately if dragging |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2183 | line | Handle slider dragging - only if we started dragging it (clicked on it first) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2185 | line | Temporarily make visible for mouse event |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2188 | line | AbstractSliderButton handles dragging internally, but we need to forward the |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2189 | line | event |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2200 | line | Not overriding Screen methods directly; return false when unhandled |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2203 | line | Handle scrollbar release |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2211 | line | Handle color picker release first (if we were dragging one) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2214 | line | Clear dragging state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2215 | line | Clear slider dragging state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2219 | line | Handle slider release (only if we were dragging it) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2225 | line | Clear dragging state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2231 | line | Handle shared color picker release - call directly like legacy version |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2233 | line | Make visible temporarily for event handling |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2238 | line | Clear dragging state |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2244 | line | Always clear dragging state on release (if not already cleared above) |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2254 | line | Handle color picker RGB/HSB field key presses - temporarily make visible if |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2255 | line | focused |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2257 | line | Check all RGB/HSB fields in the picker |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2314 | line | Handle hex field key presses - temporarily make visible if focused |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2329 | line | Handle edit box key presses - temporarily make visible if focused |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2398 | line | Handle scrolling for Default Properties panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2406 | line | Scroll speed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2413 | line | Handle scrolling for Pattern Properties panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2421 | line | Scroll speed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2428 | line | Handle scrolling for Color Swatches panel |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2436 | line | Scroll speed |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2448 | line | Allow typing in hex fields - temporarily make visible if focused |
| `src/main/java/com/kingodogo/buildscape/client/screen/PillarParticlesConfigTab.java` | 2463 | line | Allow typing in edit boxes - temporarily make visible if focused |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 31 | line | Reset color state to ensure hex colors draw accurately |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 49 | line | Use a standard fill for the color block - ensure Alpha is solid 0xFF |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 50 | line | We mask to 0xFFFFFF to be 100% sure no alpha channel bits from the int interfere |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 53 | line | Standard White/Grey border for the box itself |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 60 | line | Selection Indicator - Black border outline with a 1px gap to show "it is just a border" |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 64 | line | Draw a thin black outline slightly outside the swatch so it doesn't obscure the color |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 65 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 66 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 67 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ColorSwatchButton.java` | 68 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 8 | line | Default scale |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 27 | line | Draw background with depth |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 31 | line | Border |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 33 | line | Inner background |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 36 | line | Glossy effect if active |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 49 | line | Use the custom scale set by the parent screen |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 53 | line | Only truncate if it STILL doesn't fit (shouldn't happen with correct parent logic) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ConfigCategoryButton.java` | 63 | line | Vertically center based on the scaled height |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 9-14 | block | * * Utility class for rendering custom scrollbars with drag-to-scroll * functionality. * Supports both mouse wheel scrolling and click-drag scrolling on content * areas. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 16 | line | For Minecraft 1.18.2, use the two-argument constructor |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 21 | line | Match new texture height + padding if needed |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 23 | line | Drag state tracking |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 26 | line | Relative Y offset from thumb top when clicked |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 32-34 | block | * * Gets the scrollbar width. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 39-41 | block | * * Checks if currently dragging (scrollbar or content). |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 46-56 | block | * * Renders the custom scrollbar with texture. * * @param poseStack The pose stack for rendering * @param x X position of the scrollbar * @param y Y position of the scrollbar * @param height Height of the scrollbar track * @param scrollOffs... |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 63 | line | Push pose and disable depth test to render on top (prevent clipping) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 65 | line | Render way in front |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 66 | line | Disable depth test to prevent clipping |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 68 | line | Calculate scroll position ratio |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 71 | line | Render scrollbar track (thin vertical line in the center) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 75 | line | Render custom scrollbar thumb as a SINGLE fixed-size element |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 82 | line | The scroller texture is 8x17, render it at fixed size |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 83 | line | Use correct texture width |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 84 | line | Fixed height from texture |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 86 | line | Calculate thumb position - it moves along the track |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 87 | line | Leave space at top and bottom for the thumb |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 91 | line | Render the scroller texture as a single fixed-size element |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 92 | line | blit(poseStack, x, y, u, v, width, height, textureWidth, textureHeight) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 96 | line | Re-enable depth test |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 98 | line | Restore pose |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 101-118 | block | * * Handles mouse click on scrollbar or content area. * * @param mouseX Mouse X position * @param mouseY Mouse Y position * @param button Mouse button (0 = left click) * @param scrollbarX X position of scrollbar * @param scrollbarY Y positi... |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 127 | line | Check if clicking on scrollbar |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 131 | line | Calculate thumb position with fixed scroller height |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 132 | line | Fixed size |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 137 | line | If clicking on track (not thumb), jump to that position (centering thumb) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 139 | line | Center thumb during jump |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 145 | line | Clicking ON the thumb - store the offset from thumb top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 159-170 | block | * * Handles mouse drag for scrollbar or content dragging. * * @param mouseY Current mouse Y position * @param scrollbarY Y position of scrollbar * @param scrollbarHeight Height of scrollbar * @param maxScroll Maximum scroll value * @param v... |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 174 | line | Fixed size |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 179 | line | Target thumb Y should keep the cursor at the same relative position within the thumb |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/CustomScrollbarRenderer.java` | 195-200 | block | * * Handles mouse release to stop dragging. * * @param button Mouse button that was released * @return true if a drag was stopped, false otherwise |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 38 | line | Default to label area |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 39 | line | Headspace below label/separator |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 62 | line | width - 16 - 5 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 137 | line | Draw border around panel (Always render) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 142 | line | Exclude scrollbar area |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 144 | line | Fix: Scissor starts exactly below the header separator. This allows padding and selection borders to be visible. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 173 | line | Updated visibility check - allow items slightly above the start point for smooth scrolling |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 184 | line | Calculate centering offset matching ItemSelectionWidget |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 186 | line | Same as in calculateItemsPerRow |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 245 | line | Draw panel borders and separator |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 247 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 248 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 249 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 250 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 251 | line | Separator (moved 1px down) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 253 | line | Render scrollbar after disabling scissor so it is not clipped |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 256 | line | 4px from edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 287 | line | Calculate centering offset matching ItemSelectionWidget |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 289 | line | Same as in calculateItemsPerRow |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 372 | line | 4px from edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 377 | line | Content area for dragging (same as grid items area) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 403 | line | Updated visibility check matching renderButton |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 408 | line | Calculate centering offset matching ItemSelectionWidget |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ExistingItemsWidget.java` | 410 | line | Same as in calculateItemsPerRow |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/FlatIconButton.java` | 30 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/FlatIconButton.java` | 31 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/FlatIconButton.java` | 32 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/FlatIconButton.java` | 33 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/FlatIconButton.java` | 36 | line | Orange on hover, Gray normal |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 54 | line | Custom background - Dark style matching other widgets |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 58 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 59 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 60 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 61 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 62 | line | Background |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 64 | line | Slider handle |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 67 | line | Clamp handleX to be safe |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 71 | line | Handle color - lighter gray when hovered |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 75 | line | Draw text with custom color based on value |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 83 | line | White |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 85 | line | Green |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 87 | line | Aqua |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 89 | line | Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 91 | line | Light Purple |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 93 | line | Gold |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/IntSliderWidget.java` | 95 | line | Yellow |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 31 | line | Header area: top padding (5px) + search box height (~20px) + gap (5px) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 32 | line | Default to 1px below 20px search bar |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 33 | line | Fixed headspace between separator and items |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 74 | line | Ensure scroll offset is valid after layout changes |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 178 | line | Extra padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 193 | line | Exclude header area from scissor height |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 194 | line | Scissor Y is from bottom, so it remains the same (scissoring from bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 195 | line | usually implies bottom origin) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 196 | line | Correct logic: scissorY is bottom of rect. scissorHeight is height. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 197 | line | We want to reduce height by headerAreaHeight. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 198 | line | And we want the bottom to stay fixed. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 201 | line | Exclude scrollbar area |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 203 | line | Fix: Scissor starts exactly below the header separator. This allows padding and selection borders to be visible. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 215 | line | Start below header area with padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 221 | line | Updated visibility check - allow items slightly above the start point for smooth scrolling |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 226 | line | Calculate centering offset |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 228 | line | Same as in calculateItemsPerRow |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 246 | line | Updated hover check |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 249 | line | 0 = none, 1 = allowed, 2 = blocklisted |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 286 | line | Draw panel borders and separator |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 288 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 289 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 290 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 291 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 292 | line | Separator (moved 1px down) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 294 | line | Render scrollbar after disabling scissor so it is not clipped |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 296 | line | 4px form edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 318 | line | Updated to headerAreaHeight + padding |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 324 | line | Updated visibility check |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 329 | line | Calculate centering offset |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 331 | line | Same as in calculateItemsPerRow |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 349 | line | Updated hover check |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 374 | line | Scrollbar is now rendered in renderButton method using |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 375 | line | CustomScrollbarRenderer |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 380 | line | Updated check to ignore clicks in header area and padding gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 387 | line | 4px form edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 392 | line | Content area for dragging |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 420 | line | Updated visibility check |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 425 | line | Calculate centering offset |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 427 | line | Same as in calculateItemsPerRow |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ItemSelectionWidget.java` | 444 | line | Updated click check |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 22 | line | Contract snug sizing fits |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 36 | line | Default fallback to 20 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 63 | line | Initialize applied preset key |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 66 | line | If unamed preset exists, it might be the applied one if user just edited properties |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 67 | line | But strictly speaking, applied key is what was last applied. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 71 | line | moved |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 72 | line | up |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 193 | line | Applying explicitly sets applied key? Or assume create implies user is working on it. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 194 | line | Usually creating clears items so effectively we applied empty. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 225 | line | If we saved the unnamed preset (which was applied), update applied key to new key |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 285 | line | Fallback |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 325 | line | Allow external update of applied key ensuring sync |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 345 | line | Approx 20+2 button height |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 354 | line | Fixed 20px for vanilla button assets |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 356 | line | Calculate button width to fit 4 buttons with spacing |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 357 | line | But maybe we want them centered with a fixed width? |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 358 | line | Existing logic: int buttonWidth = (width - scaledSpacing * 5) / 4; |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 359 | line | Let's keep the width calculation but center the whole group if needed, |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 360 | line | or just ensure they fill the space nicely. The previous logic filled the width. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 361 | line | If the user wants them centered, maybe they mean the text inside? |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 362 | line | "inside there respecitive boxes" -> sounds like text inside button. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 363 | line | "centre algin the other buttons as well like create save and such" -> sounds like buttons themselves. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 364 | line | The screenshot shows "Create Save Delete Apply" left aligned? |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 365 | line | No, the screenshot shows them spread out. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 366 | line | Wait, current logic: \`x + scaledSpacing\`, \`x + scaledSpacing * 2 + buttonWidth\`... |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 367 | line | This spreads them out. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 368 | line | Let's ensuring the *vertical* alignment is centered in the bottom area. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 372 | line | Center buttons vertically in the bottom area |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 375 | line | Horizontal centering of the group: |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 377 | line | Calculate total width of the group |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 383 | line | moved up a bit more |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 385 | line | Center edit box too |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 429 | line | Draw border around panel (always) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 431 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 432 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 433 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 434 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 446 | line | Separator line directly below title row - moved 1px down |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 449 | line | Added top padding gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 451 | line | Lift |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 452 | line | buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 453 | line | up |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 475 | line | Hover check: x + 5 to width - 16 (scrollbar area) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 499 | line | Text Color Logic: |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 500 | line | Selected -> Light Red (0xFFFF5555) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 501 | line | Applied (and not selected) -> Green (0xFF55FF55) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 502 | line | Else -> White (0xFFFFFF) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 506 | line | Light Red |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 508 | line | Light Green |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 524 | line | 4px form edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 545 | line | render on top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 566 | line | Top padding gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 567 | line | Lift |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 568 | line | buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 569 | line | up |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 577 | line | 4px form edge |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 582 | line | width - 16 - 5 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 618 | line | hide options if clicked elsewhere |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 643 | line | Top padding gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 644 | line | Lift |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 645 | line | buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 646 | line | up |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 659 | line | Top padding gap |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 660 | line | Lift |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 661 | line | buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/PresetsWidget.java` | 662 | line | up |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 30 | line | Draw background with depth |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 34 | line | Border |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 36 | line | Inner background |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 65 | line | Measure component width |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 67 | line | Truncation check |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 82 | line | Measure and truncate |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 86 | line | Re-calculate X for centering based on truncated text |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 90 | line | Reset pose stack translation to use new centered X |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 99 | line | Render rich text component |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/ScaledTextButton.java` | 100 | line | Base white so colors pop |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 27 | line | Icons now rendered as text/shapes |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 62 | line | Modern Flat Design |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 63 | line | Background: Dark Grey |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 64 | line | Selected: Green Accent |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 65 | line | Border: 1px constant |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 68 | line | Green when selected |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 71 | line | Draw background |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 72 | line | Border |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 73 | line | Background |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 76 | line | Center text vertically (font height 8) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 79 | line | Render Chest Icon |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 81 | line | Item is 16x16. CenterY is calculated for text (height 8). |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 82 | line | Button Center Y = y + height/2. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 83 | line | Item Y = Button Center Y - 8. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 84 | line | Text CenterY = y + (height - 8) / 2 = Button Center Y - 4. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 85 | line | So Item Y = Text CenterY - 4. |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 92 | line | A for All |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 95 | line | M for Mod |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 100 | line | Optional: Add glow or underline if selected? |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 101 | line | kept simple as requested "modern panel like" |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/SortToggleButton.java` | 114 | line | This is called from mouseClicked, but we want modifiers, so we'll handle it there instead if possible |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 25 | line | Items start below this area |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 26 | line | Default to 1px below 20px search bar |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 27 | line | Headspace between separator line and tag list |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 44 | line | Scrolling text state |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 151 | line | Tags should start below the header area (label, search box, buttons) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 152 | line | Tags should start below the header area (label, search box, buttons) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 155 | line | Account for bottom margin |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 166 | line | Clip content area: Include the padding space for selection borders |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 175 | line | +1 for smooth scrolling |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 178 | line | Tag list starts below separator + padding gap (matching items grid) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 180 | line | Total width - 5px left padding - 16px right reserved space = width - 21 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 191 | line | Updated visibility check - allow items slightly above the start point |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 227 | line | Scrolling text logic |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 234 | line | Speed factor |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 237 | line | Sine wave scrolling: 0 -> max -> 0 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 238 | line | (1 - cos(t)) / 2 ranges from 0 to 1 to 0 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 242 | line | Clip text to button bounds to prevent leaking |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 244 | line | Approx bounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 248 | line | Use intersection with existing scissor (list area) to remain safe |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 249 | line | But simplified: just enabling scissor for the text line is usually enough if |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 250 | line | strictly contained |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 251 | line | Better: Use viewport intersection logic or just a tighter scissor |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 253 | line | Strict clipping for the text |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 257 | line | Apply scissor for text |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 266 | line | Restore list scissor |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 291 | line | Draw panel borders and separator |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 293 | line | Top |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 294 | line | Bottom |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 295 | line | Left |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 296 | line | Right |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 297 | line | Separator (moved 1px down) |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 300 | line | Scrollbar X = width - 4px gap - 8px width = width - 12 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 314 | line | Updated check to ignore clicks in header area |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 327 | line | Content width = width - 16px reserved - 5px left padding = width - 21 |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 343 | line | Use dynamic value |
| `src/main/java/com/kingodogo/buildscape/client/screen/widget/TagsSelectorWidget.java` | 363 | line | Updated visibility check |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 11-18 | block | * * WbRenderer - drawing helpers for the Builders Workbench screens. * * <p>The static part of each builder tab (panel, frames, slot backgrounds and the * player inventory) is baked into a single background sheet. Everything that has * more... |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 21 | line | Backgrounds |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 25 | line | Tabs - each sprite already contains the plate and its icon, one per state. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 33 | line | Title graphics - baked lettering, drawn in place of the vanilla font. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 37 | line | Copy arrow |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 41 | line | Filter buttons |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 61 | line | Sizes |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 62 | block | * Both builder background sheets are 256x256 with the artwork anchored at (0,0). |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 64 | block | * Tab sprites are 17x17 with the plate and icon already composed by the artist. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 70 | block | * Every title graphic is baked at (0,0) of a 128x16 sheet. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 74 | line | Title graphic metrics on their 128x16 sheets: {width, height, bodyY, bodyHeight}. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 75 | line | bodyY/bodyHeight describe the block of actual letters. They differ from the full |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 76 | line | ink box when a glyph reaches above the caps line - the pouch apostrophe occupies |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 77 | line | row 0 on its own, and centring the full box on that row visibly drops the whole |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 78 | line | caption. Centring the letter body instead makes all three captions sit alike. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 86-89 | block | * * Blits with explicit float UVs. Needed for the partially filled arrow, where the * horizontal UV has to be cut at an arbitrary fraction instead of a whole pixel. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 104-107 | block | * * Blits a full builder background out of its 256x256 sheet. The screen passes its * own imageWidth / imageHeight, so the sheet stays the single source of layout truth. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 113-116 | block | * * Draws one tab. The icon is part of the sprite, so nothing is composed or scaled * here - picking the right state sprite is the whole job. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 159-167 | block | * * Draws a title graphic at native size, centred on the banner rectangle. * * <p>No scaling: the lettering is pixel art and any non-integer factor turns it to * mush. A graphic wider than its banner therefore overhangs rather than shrinkin... |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 172 | line | Integer division, not Math.round: when the leftover is odd the extra pixel has |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 173 | line | to land somewhere, and floor puts it on the right the way vanilla centres text. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 174 | line | The banner interior is 77px while every title is an even width, so that |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 175 | line | leftover is always odd today - widening the banner to 78 in the artwork makes |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 176 | line | all three captions land dead centre with no code change. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 178 | line | Position the letter body in the middle, then step back to the sheet's origin. |
| `src/main/java/com/kingodogo/buildscape/client/screen/workbench/WbRenderer.java` | 185-188 | block | * * Draws the idle arrow, then overlays the active variant clipped to {@code progress} * (0..1) so the arrow appears to fill from left to right while copying. |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 37 | line | One-time widget creation |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 100 | line | leftPanel (Player Rules) |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 106 | line | rightPanel (World Rules) |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 165 | line | Draw Left Box (Player Rules) |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 171 | line | Draw Right Box (World Rules) |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 193 | line | Update button states to ensure they reflect sync packets and local settings |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 204 | line | A simple inner class for a toggle button with BuildScape visuals |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 240 | line | Draw custom button background |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 241 | line | 50% dark |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 243 | line | Draw border |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 249 | line | Draw toggle status bar |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 255 | line | Draw text |
| `src/main/java/com/kingodogo/buildscape/client/screen/WorldSettingsConfigTab.java` | 274 | line | Render lock icon if inactive |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 22-27 | block | * * Spawns smoke vent particles for vents beyond the vanilla animateTick range (~16 blocks). * Minecraft's animateTick only fires for blocks within 16 blocks of the player, so distant * smoke vents would appear inactive without this handler... |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 35 | line | animateTick covers blocks within this range, so we skip them to avoid double-spawning |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 50 | line | Use the client's render distance in chunks to determine how far to scan |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 70 | line | Skip vents within animateTick range — those are already handled by animateTick |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 74 | line | Only spawn from top/single parts (same logic as animateTick) |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 83 | line | Only spawn if active |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 86 | line | Since client tick runs 20 times per second, we reduce the spawn probability |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 87 | line | to match the random, less frequent animateTick calls (approx. once per 1-1.5 seconds) |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 88 | line | 5% chance per tick |
| `src/main/java/com/kingodogo/buildscape/client/SmokeVentParticleHandler.java` | 90 | line | Spawn particle at same positions as animateTick |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 20 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 21 | line | COLOR PALETTE (Hex Codes 0xRRGGBB) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 22 | line | Easily modify this hex value to customize the pouch tooltip tint: |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 23 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 51 | line | 32px |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 56 | line | 176px |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 67 | line | 1. Setup Texture & Golden Color Tint |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 73 | line | 2. Render 9-Slice Background Frame |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 74 | line | Corners (7x7) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 80 | line | Top & Bottom Edges (9 columns x 18px) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 86 | line | Left & Right Edges (1 row x 18px) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 90 | line | Slots Grid (1 row of 9 slots x 18px) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 95 | line | Reset Shader Color for Item Rendering |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/BuildersPouchTooltipData.java` | 98 | line | 3. Render Item Stacks in Slots |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 27 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 28 | line | COLOR PALETTE (Hex Codes 0xRRGGBB) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 29 | line | Sampled directly from GUI textures. Easily modify hex values here: |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 30 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 94 | line | 68px |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 99 | line | 176px |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 110 | line | 1. Setup Texture & Shulker Color Tint |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 116 | line | 2. Render 9-Slice Background Frame |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 117 | line | Corners (7x7) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 123 | line | Top & Bottom Edges (9 columns x 18px) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 129 | line | Left & Right Edges (3 rows x 18px) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 135 | line | Slots Grid (9x3 slots x 18px) |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 142 | line | Reset Shader Color for Item Rendering |
| `src/main/java/com/kingodogo/buildscape/client/tooltip/ShulkerBoxTooltipData.java` | 145 | line | 3. Render Item Stacks in Slots |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 43 | line | Cache |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 56 | line | Reset |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 100 | line | Succeeded |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 102 | line | Reset |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 109 | line | Update break progress (0-9) |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 130 | line | Check what we are looking at NOW |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 140 | line | Render count with block name |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 149 | line | Position above crosshair |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 153 | line | Green text |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 174 | line | Update cache |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 189 | line | Render |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 202 | line | Cyan/Aqua color |
| `src/main/java/com/kingodogo/buildscape/client/TreeChopHandler.java` | 246 | line | Check neighbors |
| `src/main/java/com/kingodogo/buildscape/client/workbench/ClientBlockColorCatalog.java` | 35-39 | block | * * Lazily samples the baked block models that Minecraft is already using. The * catalog is rebuilt only after a resource reload and never exists on a * dedicated server. |
| `src/main/java/com/kingodogo/buildscape/client/workbench/ClientBlockColorCatalog.java` | 166 | line | Some modded tint handlers require a live level and position. |
| `src/main/java/com/kingodogo/buildscape/client/WrenchClientHandler.java` | 60 | line | Cancel default outline box |
| `src/main/java/com/kingodogo/buildscape/client/WrenchClientHandler.java` | 75 | line | Render glowing highlight box around block |
| `src/main/java/com/kingodogo/buildscape/client/WrenchClientHandler.java` | 85 | line | Bright copper glowing outline (R=1.0, G=0.6, B=0.1) |
| `src/main/java/com/kingodogo/buildscape/client/WrenchClientHandler.java` | 91 | line | Base bounding box |
| `src/main/java/com/kingodogo/buildscape/client/WrenchClientHandler.java` | 99 | line | Slightly inflated outer glow line for vibrant visual feedback |
| `src/main/java/com/kingodogo/buildscape/config/BuildscapeClientConfig.java` | 104 | line | Migrate from legacy config/buildscape.cfg if new config/buildscape/buildscape.cfg does not exist yet |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 19-22 | block | * * Configuration manager for equipped cosmetics and player rules. * Persists data in a private 'buildscape/data' directory to keep the config folder clean. |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 27 | line | Cache of player UUID string to their equipped cosmetics by slot |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 30 | line | Cache of player UUID string to cosmetic colors (cosmeticId -> hex color string) |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 33 | line | Cache of player UUID string to creative tree breaker boolean |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 36 | line | Cache of player UUID string to shulker preview boolean |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 39 | line | Color picker window position |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 44 | line | 1. First, ensure our private data directory exists |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 48 | line | 2. Migrate from legacy locations |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 51 | line | 3. Load global settings from private storage |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 62-64 | block | * * @return The "private" data directory for BuildScape (not in config). |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 78-80 | block | * * Handles migration from BOTH the old JSON config AND the temporary NBT config location. |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 86 | line | 1. Move any .dat files from config/buildscape/ to buildscape/data/ |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 96 | line | Already exists in new location |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 105 | line | 2. Migrate from the legacy JSON format if it still exists |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 183-185 | block | * * Internal method to load player data from their specific NBT file. |
| `src/main/java/com/kingodogo/buildscape/config/CosmeticsConfig.java` | 240 | line | default ON |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 7-10 | block | * * Data class for storing GUI element configuration. * Stores position, size, and scale information for GUI elements. |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 13-15 | block | * * Configuration for a single GUI element/widget. |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 35-38 | block | * * Percentage-based positioning (0.0 to 1.0, e.g., 0.2 = 20%) * If set, these override x/y/width/height when calculating positions |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 51-53 | block | * * Additional custom properties that can be stored for extensibility |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 71-73 | block | * * Copy constructor |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 85-87 | block | * * Screen-level configuration |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 91-94 | block | * * Map of element IDs to their configurations * Element IDs are widget names or identifiers like "itemSelectionWidget", "searchBox", etc. |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 98-100 | block | * * Screen-level configuration |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 103 | line | 0 means use default/screen width |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 106 | line | 0 means use default/screen height |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 118 | line | 0 means use default |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 121 | line | 0 means use default |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 126-128 | block | * * Get configuration for an element, creating a default if it doesn't exist |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 133-135 | block | * * Get configuration for an element, returning null if it doesn't exist |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 140-142 | block | * * Set configuration for an element |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 147-149 | block | * * Remove configuration for an element |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigData.java` | 154-156 | block | * * Check if an element configuration exists |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 13-16 | block | * * Manager for loading and saving GUI configuration files. * Handles JSON serialization/deserialization for GUI layouts. |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 25-27 | block | * * Get the config directory for GUI configs |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 37-41 | block | * * Get the file path for a tab's GUI config * @param tabName The name of the tab (e.g., "PillarItems", "PillarParticles") * @return The file for the GUI config |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 47-49 | block | * * Sanitize a tab name to be a valid file name |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 51 | line | Replace invalid characters with underscores |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 55-59 | block | * * Load GUI configuration for a tab * @param tabName The name of the tab * @return The GUI configuration data, or a new empty config if file doesn't exist |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 63 | line | Return cached config if available |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 77 | line | Ensure maps are initialized |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 91 | line | Cache the config |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 96-100 | block | * * Save GUI configuration for a tab * @param tabName The name of the tab * @param config The GUI configuration data to save |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 119 | line | Update cache |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 130-133 | block | * * Clear the cache for a specific tab (useful when reloading) * @param tabName The name of the tab |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 138-140 | block | * * Clear all cached configs |
| `src/main/java/com/kingodogo/buildscape/config/GuiConfigManager.java` | 145-147 | block | * * Get the singleton instance |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 36 | line | ── O(1) position index ─────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 37 | line | getPillarDataByPosition() previously scanned all pillarData entries. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 38 | line | On servers with hundreds of pillars this became O(n) per serverTick. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 39 | line | The index maps "dimension:x:y:z" → pillarId so lookups are O(1). |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 46 | line | Track if we had colors when we loaded |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 47 | line | Flag to indicate data came from server |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 48 | line | Flag to allow saving empty pillar data (e.g., user removed all) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 52 | line | 5 seconds after world load |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 53 | line | Flag to prevent saving during recovery |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 66-69 | block | * * Reset world cache directory and CLEAR data - makes Pillar IDs world/server specific. * Called on world unload/player logout. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 283-285 | block | * * Full reset - clears all data. Only called on server stop. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 294 | line | keep index in sync |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 303-306 | block | * * Schedule recovery to run after world load. * Recovery will run automatically after RECOVERY_DELAY_MS. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 312-315 | block | * * Check if scheduled recovery should run and execute it. * Called from server tick event. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 342 | line | false = don't clear colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 417 | line | Always orient to the bottom of the stack for consistent ID mapping |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 434 | line | O(1) fast path via position index |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 442 | line | Wrong variant prefix — evict and recreate |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 447 | line | dangling reference — clean up |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 454 | line | Populate initial data from the world (type, displayed item from stack, etc.) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 461 | line | Sync colors too if they exist in the world but not yet in the manager |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 471 | line | keep index in sync |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 473 | line | IMPORTANT: Don't save during recovery - recovery will save once at the end |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 474 | line | This prevents saving empty colors repeatedly during recovery |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 515 | line | Sync items and type whenever dyeing to ensure instant GUI reflection |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 522 | line | Lock current pattern settings if they are currently following global defaults |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 526 | line | Safe fallback |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 537 | line | O(1) lookup via position index instead of iterating all entries |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 551 | line | Remove from position index too |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 554 | line | Allow saving empty file if user removes all pillars |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 589 | line | O(1) removal via position index |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 618 | line | Sync all settings from NBT whenever item is updated |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 808-810 | block | * * Process loaded data and merge with existing data. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 814 | line | IMPORTANT: Preserve existing entries when reloading (especially early-registered item frames) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 817 | line | Clear map and index, we will rebuild them from file + existingData merge |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 842 | line | CRITICAL: Preserve colors from file - file is the source of truth for GUI |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 843 | line | GSON should have populated data.dyeColors from JSON |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 844 | line | Save the original GSON-deserialized colors IMMEDIATELY |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 847 | line | GSON deserialized something - preserve it exactly as-is |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 853 | line | CRITICAL: File colors take absolute priority - use them if they exist |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 855 | line | File has colors - ALWAYS use them, ignore everything else |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 858 | line | File is empty or null - initialize and check manager |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 864 | line | Use manager colors if they exist |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 869 | line | If both are empty, keep empty (will sync from NBT later) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 887 | line | FINAL SAFEGUARD: Ensure colors are preserved before putting into map |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 888 | line | Double-check that colors are set (file colors take priority) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 890 | line | If colors are empty, check if we have original file colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 899 | line | Keep position index in sync with loaded data (respect facing for ItemFrames) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 930 | line | Preserve and re-add entries that were registered early but aren't in the file yet |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 936 | line | Also add to index |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 945 | line | Track if we had colors when we loaded |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 970 | line | IMPORTANT: Don't sync from NBT here - block entities might not be loaded yet |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 971 | line | Colors will be synced from NBT during recovery or when GUI opens |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 972 | line | This prevents clearing colors before block entities are ready |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 974 | line | Schedule recovery to run after world load (to add any missing pillars and sync colors) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1003 | line | IMPORTANT: Load from main file only (pillar-ids.dat) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1004 | line | Backup file is separate and only saved on world save/server close |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1010 | line | Load from main file first |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1022 | line | CRITICAL: If main file has empty colors, try backup file (backup is preferred for GUI) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1023 | line | Check if main file has colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1034 | line | If main file has no colors, try backup file |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1041 | line | Check if backup has colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1050 | line | If backup has colors, use it (backup is preferred for GUI) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1062 | line | If file failed or doesn't exist, start fresh |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1072 | line | Process loaded data |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1089-1091 | block | * * Load data from a specific file. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1105 | line | IMPORTANT: Don't save during recovery - recovery will save once at the end after syncing colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1119 | line | Log what we're saving |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1128 | line | SAFEGUARD: Only prevent save if we have a TOTAL loss of data (count dropped to 0) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1129 | line | AND we know the file previously had lots of data. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1130 | line | UNLESS the user explicitly removed all pillars (allowEmptySave = true) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1132 | line | Check the file directly to see if it has colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1149 | line | Ignore - allow save to proceed if we can't check file |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1152 | line | If we had data and now have 0, this might be a corruption/fail-safe trigger |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1153 | line | Only prevent if we haven't finished loading our data yet |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1155 | line | BuildScape.getLogger().warn("PillarIdManager: TOTAL DATA LOSS DETECTED - Preventing save and reloading!"); |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1161 | line | Reset the flag after saving |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1167 | line | Save to main file only (backup file is saved separately on world save/server close) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1170 | line | Update timestamps from main file |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1177 | line | Sync with all clients instantly |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1317 | line | Prevent saves during recovery |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1403 | line | Check if pillar already exists in manager (by ID) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1407 | line | Pillar exists - update it, don't create duplicate |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1423 | line | Update colors if NBT has colors (preserve manager colors if NBT is empty) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1428 | line | NBT has colors - sync them (only if different) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1452 | line | If NBT doesn't have colors, preserve manager colors (do nothing) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1458 | line | Don't increment recoveredCount - this is an update, not a new recovery |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1462 | line | New pillar - create data |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1498 | line | IMPORTANT: Sync colors from NBT BEFORE saving |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1499 | line | This ensures colors are loaded from NBT and saved to file |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1502 | line | Allow final save after syncing colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1510 | line | Don't call syncAllLoadedPillars here - we already synced colors above |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1517 | line | Always reset flag, even if recovery failed |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1526-1532 | block | * * Force save pillar data regardless of player count or server state. * Used during server shutdown when players have already disconnected * and the server is flagged as stopping. * CRITICAL: Uses cachedWorldSaveDir directly because getDat... |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1548 | line | Use cached world save dir directly - getDataDir() would fail |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1549 | line | because playerCount==0 during shutdown |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1552 | line | Fallback: try to get from server path directly |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1565 | line | Last resort |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1577 | line | Also save backup |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1586 | line | Sync with all clients instantly |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1598 | line | Check main file only (backup file is separate, only saved on world save/server close) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1612-1614 | block | * * Save pillar data to a specific file. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1730 | line | Sync ALL data from manager (colors, pattern, speed, spread, etc.) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1753-1755 | block | * * Save the backup file (only called on world save/server close). |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1776 | line | Save to backup file only |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1786-1791 | block | * * Syncs ALL settings (colors, pattern, speed, spread, intensity, max_particle_color) * FROM block entity NBT TO manager for all loaded pillars. * This ensures the manager has all settings that exist in NBT after world load, * so the GUI c... |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1797 | line | syncColorsFromNBTToManager should run even when players are not online (e.g. during startup/shutdown) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1798 | line | to ensure manager's data is consistent with the world. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1800 | line | IMPORTANT: Don't sync if manager hasn't loaded yet - this prevents clearing colors before load |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1808 | line | Track pattern/item changes separately |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1815 | line | Iterate through all pillar data in manager |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1820 | line | Preserve existing colors count for logging |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1827 | line | Chunk not loaded - preserve manager colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1834 | line | Chunk not ready - preserve manager colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1840 | line | Chunk not fully loaded - preserve manager colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1847 | line | No block entity - preserve manager colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1852 | line | Find the bottom of the stack to get the actual block entity with colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1857 | line | No bottom block entity - preserve manager colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1862 | line | Get colors from NBT (block entity at bottom of stack) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1865 | line | IMPORTANT: Only sync if NBT has colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1866 | line | If NBT is empty or null, preserve manager colors (don't clear them) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1868 | line | Check if manager colors match NBT colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1871 | line | Manager has no colors, NBT has colors - sync |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1874 | line | Different number of colors - sync |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1877 | line | Compare colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1889 | line | Sync colors FROM NBT TO manager |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1898 | line | Colors already match - preserve |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1902 | line | Also sync pattern settings and items from NBT |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1907 | line | NBT is empty or null - preserve manager colors (do nothing) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1911 | line | Still try to sync pattern settings and items even if colors are empty |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1917 | line | Error accessing block entity - preserve manager colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1928 | line | Save if ANY data changed (colors, patterns, or items) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1941-1945 | block | * * Loads colors directly from NBT for all loaded pillar block entities. * This is called after loading the file to populate colors from the actual world data. * Colors are loaded directly from NBT, not from the file. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1963 | line | Iterate through all pillar data in manager |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1989 | line | Find the bottom of the stack to get the actual block entity with colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 1997 | line | Get colors directly from NBT |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2000 | line | Load colors from NBT into manager (if NBT has colors) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2001 | line | If NBT is empty, preserve colors from file |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2003 | line | NBT has colors - use them (overwrite file colors) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2012 | line | NBT is empty - preserve colors from file (if any) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2027 | line | Only save if colors were actually loaded from NBT |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2028 | line | Don't save if no colors were loaded - this preserves file colors |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2041-2044 | block | * * Syncs pattern and item settings from a BlockEntity to a PillarData object. * Returns true if any changes were made. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2055 | line | Sync pillar type |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2062 | line | Sync pattern |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2071 | line | Sync pattern speed |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2080 | line | Sync pattern spread |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2089 | line | Sync pattern intensity |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2098 | line | Sync max particle colors (from the number of colors in NBT) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2108 | line | Sync displayed item - item is always moved to the top of the stack |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2133 | line | Sync item yaw (rotation) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2147-2151 | block | * * Syncs pattern settings (pattern, speed, spread, intensity, max_particle_color, displayed item) * FROM block entity NBT TO manager. * Returns true if any data was changed and needs to be saved. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2154 | line | Create a deep copy to ensure colors are preserved |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2173 | line | Copy displayed item data |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2177 | line | Deep copy colors list |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2196 | line | Rebuild position index to keep in sync |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2214-2218 | block | * * Clears pillar data for server sync. Called on client when receiving data from server. * IMPORTANT: This prepares the client to receive fresh data from the server. * The isServerSynced flag will be set to true after all data is loaded. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2223 | line | Don't set isServerSynced here - wait until data is fully loaded |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2227-2229 | block | * * Adds pillar data from server sync packet. Called on client. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2247-2250 | block | * * Registers a pillar block entity with the manager. * Called by PillarBlockEntity on load and place. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2269 | line | Check if already registered |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2272 | line | New pillar discovered from world/NBT |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2275 | line | Sync current state from BE (colors, pattern, item, etc.) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2286 | line | Save if on server |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2291 | line | Already exists - update position index just in case it moved/was reindexed |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2294 | line | Sync all settings from BE to manager |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2303-2305 | block | * * Registers an item frame with the manager. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2314 | line | Generate and save ID if missing |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2339 | line | Set type for icon display |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2343 | line | Set displayed item |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2359 | line | Always ensure type and facing are set even if re-registering |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2378 | line | Update item |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2398-2400 | block | * * Registers a colored item frame with the manager. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2409 | line | Generate and save ID if missing |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2434 | line | Set type for icon display based on variant |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2443 | line | Set displayed item |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2461 | line | Always ensure type and facing are set even if re-registering |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2485 | line | Update item |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2504-2508 | block | * * Marks the manager as loaded. Called after syncing from server. * This sets both hasLoaded and isServerSynced flags to indicate * that the client has received complete data from the server. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2514-2516 | block | * * Gets a list of all pillar data for syncing to clients. |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2530 | line | Per-pillar config options (optional, defaults to global config if not set) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2531 | line | null means use global config |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2532 | line | null means use global config |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2536 | line | Max number of colors for this pillar (1-5) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2538 | line | Display item (serialized as string for JSON compatibility) |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2539 | line | Format: "minecraft:item_id" |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2540 | line | Format: "minecraft:stone_pillar" |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2541 | line | Direction name for item frames |
| `src/main/java/com/kingodogo/buildscape/config/PillarIdManager.java` | 2542 | line | Rotation of displayed item |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 41 | line | ── Cheap-read snapshot for the hot tick path ──────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 42 | line | clientTick() reads config dozens of times per second. Rather than going |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 43 | line | through the full get() machinery (isClientConnectedToServer reflection + |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 44 | line | file-stat check on two files) on every call, we keep a volatile snapshot |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 45 | line | that get() refreshes whenever the underlying config actually changes. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 48-52 | block | * * Fast path for the hot client-tick loop: returns the last loaded config * without any I/O or reflection. Falls back to the full get() path only * until the first non-null snapshot is available. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 58 | line | ── isClientConnectedToServer cache ──────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 59 | line | Class.forName + 3 reflection invocations is very expensive to call on |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 60 | line | every tick. Cache the result and refresh at most once per ~3 seconds. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 63 | line | 3 s |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 65 | line | ── File-stat throttle ─────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 66 | line | file.lastModified() + file.length() on two files was being called on |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 67 | line | every invocation of get() which happens every client tick per pillar. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 68 | line | Throttle to once every 2 seconds (40 game ticks / 2000 ms). |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 70 | line | 2 s |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 84 | line | invalidate per-Item match cache on every config reload |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 89 | line | ── matches() cache ─────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 90 | line | matches() iterates the items set, calling new ResourceLocation() and a full |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 91 | line | tag-registry lookup for every tag-prefixed entry, every 5 client ticks per |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 92 | line | active pillar. The result only depends on the Item type (registry key + |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 93 | line | tags) — not on the ItemStack NBT — so we can safely cache Boolean per Item. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 94 | line | The cache is cleared whenever the config reloads so hot-reload still works. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 163 | line | ── Throttled file-stat check ────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 164 | line | Only hit the filesystem to check for config changes every 2 s. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 165 | line | In steady-state, most calls return immediately without any I/O. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 249 | line | White |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 250 | line | Orange |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 251 | line | Light Blue |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 266 | line | new server config — invalidate match cache |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 311 | line | Simple debounce: wait a bit to let file write finish |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 325 | line | Check if it actually reloaded by comparing timestamps/sizes isn't |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 326 | line | easy here since loadPropertiesInternal updates them. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 327 | line | But loadPropertiesInternal is only called if we are here. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 328 | line | To properly debounce, we should check if enough time passed since |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 329 | line | last reload/notify. |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 338 | line | Debounce notifications: only notify if > 500ms since last |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 339 | line | notification |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 727 | line | White |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 728 | line | Orange |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 729 | line | Light Blue |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 895 | line | Auto-add any new default vanilla items that were added in mod updates |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 986 | line | Check for auto-update of Vault Hunters items |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 1352 | line | Update the server's global config when changed on client |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 1361 | line | Don't save on client disk when connected to server |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 1393 | line | Update the server's global config when changed on client |
| `src/main/java/com/kingodogo/buildscape/config/PillarParticleConfig.java` | 1402 | line | Don't save on client disk when connected to server |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 7-12 | block | * * Handles resetting pillar block entities to their default state when * pillar IDs are removed from the manager. This ensures that pillars * act as if freshly placed (no custom colors/patterns) while keeping * the displayed item intact. |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 15-22 | block | * * Resets a pillar block entity to its default state (freshly placed). * Clears all custom particle colors, patterns, and settings from NBT, * but keeps the displayed item. * * @param dimension The dimension key (e.g., "minecraft:overworld... |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 34 | line | Execute on server thread to ensure thread safety |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 35 | line | This is necessary because removal might be called from client-side GUI |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 38 | line | Find the level for this pillar's dimension |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 41 | line | Check if chunk is loaded |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 46 | line | Get the block entity at the bottom of the stack |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 49 | line | Find the bottom of the stack (in case pos is not the bottom) |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 52 | line | Reset ALL connected pillar blocks in the stack |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 59 | line | Reset all custom particle settings to default (freshly placed state) |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 65 | line | Safety check to prevent infinite loops |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 75 | line | Error silently handled |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 79 | line | Silently handle errors (e.g., world not loaded, chunk not available) |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 80 | line | This is expected if the pillar is in an unloaded chunk |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 84-89 | block | * * Resets a pillar block entity's particle-related fields to default values. * This method directly modifies the block entity's internal state. * * @param pillarBE The pillar block entity to reset |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 95 | line | Reset all custom particle settings to default (freshly placed state) |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 96 | line | This clears colors, patterns, and all related settings from NBT |
| `src/main/java/com/kingodogo/buildscape/config/PillarResetHandler.java` | 100-105 | block | * * Resets a pillar using PillarData from the manager. * This is called when a pillar ID is removed from the manager. * * @param data The PillarData containing dimension and position info |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 32 | line | Track last applied preset |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 33 | line | Special key for unsaved changes |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 49 | line | Load presets |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 53 | line | Load last applied preset key |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 58 | line | Load preset (skip unnamed as it's temporary) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 65 | line | Skip invalid preset |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 78 | line | Check for auto-update of "default" preset with new mod items |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 83 | line | Get all default items (includes vault items if mod is loaded) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 87 | line | Only auto-add vault items that are missing (respect user removal of other |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 88 | line | items) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 102 | line | Initialize lastAppliedPreset if not set |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 129 | line | Save presets and last applied preset (but not unnamed preset) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 144 | line | Call this after PillarParticleConfig has loaded to apply default preset |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 146 | line | Always apply default preset on startup if items are empty |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 149 | line | First time - apply default preset |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 157 | line | Create default preset with default items |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 296 | line | Always include default first |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 300 | line | Include unnamed preset if it exists (for display) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 304 | line | Then add custom presets (up to 5 total including default) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 305 | line | Sort by key to maintain consistent order |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 326 | line | Include unnamed preset if it exists |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 330 | line | Add custom preset keys in sorted order |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 348 | line | Can't modify default or unnamed |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 351 | line | Check if we're at max presets (exclude default and unnamed from count) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 359 | line | If this is a new preset and we're at max, don't allow |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 390 | line | Can't delete default or unnamed |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 407 | line | Track last applied preset (but not unnamed) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 408 | line | Save the last applied preset info |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 418 | line | Save current items as unnamed preset (unsaved changes) |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 420 | line | Don't save to file - this is temporary |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 436 | line | Auto-apply unnamed preset if it exists, otherwise apply last applied preset |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 442 | line | First time - apply default |
| `src/main/java/com/kingodogo/buildscape/config/PresetsConfig.java` | 450 | line | Generate a unique key for a new preset |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameAttachment.java` | 16-19 | block | * * Manages storing, querying, and updating cosmetic frame attachments on signs. * Uses Forge's BlockEntity persistent data to ensure survival through saves, unloads, and reloads. |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameAttachment.java` | 24-27 | block | * * Checks whether the given blockstate and block entity represent a valid sign. * Generically detects any vanilla or modded standing/wall sign. |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameAttachment.java` | 36-38 | block | * * Retrieves the attached frame type from a sign block entity. |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameAttachment.java` | 50-52 | block | * * Checks if the sign has any cosmetic frame attached. |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameAttachment.java` | 57-59 | block | * * Sets or removes the frame on a sign block entity, persisting data and syncing to clients. |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameInteractionHandler.java` | 20-23 | block | * * Handles applying and removing cosmetic frames on signs. * Prioritizes normal sign behavior; only intercepts when applying or removing a frame. |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameInteractionHandler.java` | 47 | line | CASE 1: REMOVING THE FRAME (Right-Click a framed sign with Shears) |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameInteractionHandler.java` | 72 | line | CASE 2: APPLYING THE STRINGLIGHT FRAME (Right-Click with Stringlight Frame item) |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameInteractionHandler.java` | 91 | line | Sign already has a frame attached; prevent applying a second frame |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameInteractionHandler.java` | 98 | line | CASE 3: Normal sign interaction (editing, dye, glow ink, commands, etc.) - do not intercept! |
| `src/main/java/com/kingodogo/buildscape/cosmetic/sign/SignFrameType.java` | 13-16 | block | * * Extensible cosmetic frame types for signs. * Currently supports STRINGLIGHT, structured so future cosmetic designs can be added cleanly. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosArmor.java` | 12 | line | Base class for all custom body/armor cosmetics (Chest, Legs, Feet). |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosArmor.java` | 13 | line | Handles multiple model parts (e.g. left arm, right arm, body). |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosArmor.java` | 30 | line | Default: static cosmetic that moves with bones |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosArmor.java` | 45 | line | Optional: override this to apply custom transformations before rendering. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosArmor.java` | 47 | line | Default: no transform |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosChest.java` | 10-12 | block | * * Base class for all custom chest cosmetics. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosChest.java` | 37 | line | Body |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosChest.java` | 39 | line | Arms |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosFeet.java` | 10-12 | block | * * Base class for all custom feet (boots) cosmetics. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosHead.java` | 10-13 | block | * * Base class for all custom head cosmetics. * Extend this class and register it in CosmeticManager to add new custom head models. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosHead.java` | 30 | line | Default: static cosmetic |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosHead.java` | 38-40 | block | * * Optional: override this to apply custom transformations before rendering. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosHead.java` | 42 | line | Default: no transform |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosLegs.java` | 10-12 | block | * * Base class for all custom legs cosmetics. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosLegs.java` | 15 | line | Waist/Belt area |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 8-11 | block | * * Manages cosmetic registration and provides access to all available cosmetics. * Automatically registers built-in cosmetics and provides dev access. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 15 | line | All registered cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 18 | line | Cosmetic metadata (name, description, tier, etc.) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 21 | line | Particle shape mapping (cosmeticId -> shape type) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 24 | line | Universal Cosmetics Registries |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 30 | line | Default cosmetics that are free for everyone (particle trails) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 51-53 | block | * * Register all built-in cosmetics. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 55 | line | Register particle trail cosmetics with different shapes |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 56 | line | Default / Dyeable Particles |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 62 | line | Other Default Particles |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 66 | line | Changed from "cherry_leaves" to "cherry" to use ModParticles.CHERRY |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 70 | line | Custom Particles (Redeemable - not added to defaultCosmetics) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 77 | line | Register gear cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 80 | line | Register Annoying Kingo Pet (unlocked for everyone) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 83 | line | Unlocked for everyone |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 86 | line | Register block cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 91-93 | block | * * Register a particle trail cosmetic (default = free for everyone). |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 96 | line | Particle trails are free for everyone |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 102-104 | block | * * Register a particle trail cosmetic that requires redemption (not free by default). |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 107 | line | NOT added to defaultCosmetics - requires redemption code |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 113-115 | block | * * Register a particle wings cosmetic (free for everyone). |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 118 | line | Free for everyone |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 125-127 | block | * * Get particle shape for a cosmetic. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 132-134 | block | * * Check if a cosmetic supports color customization. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 139 | line | Particle wings - all shapes support color |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 144 | line | For particle trails, only specific shapes support color |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 150 | line | Only Sparkle and Heart shapes support colors |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 151 | line | Cherry, Cake, Snowflake trail, Firework, Note, Bubble, Cherry Leaves are NOT colorable |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 155-157 | block | * * Register an item cosmetic. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 164-166 | block | * * Register a custom head/armor cosmetic with a custom model. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 173-175 | block | * * Register a custom armor cosmetic (Chest, Legs, Feet) with a custom model. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 182-184 | block | * * Register a universal head cosmetic. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 190-192 | block | * * Register a universal chest cosmetic. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 198-200 | block | * * Register a universal legs cosmetic. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 206-208 | block | * * Register a universal feet cosmetic. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 214-216 | block | * * Get a universal head cosmetic by ID. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 221-223 | block | * * Get a universal chest cosmetic by ID. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 228-230 | block | * * Get a universal legs cosmetic by ID. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 235-237 | block | * * Get a universal feet cosmetic by ID. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 242-244 | block | * * Get all registered cosmetics. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 249-251 | block | * * Get metadata for a cosmetic. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 256-258 | block | * * Check if a cosmetic is registered. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 263-265 | block | * * Check if a cosmetic is a default (free for everyone). |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 270-272 | block | * * Get all default cosmetics (free for everyone). |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 277-281 | block | * * Get unlocked cosmetics for offline/default use. * Returns only default cosmetics (particle trails). * For full unlocks including redeemed items, use SupportersTabState which gets data from the API. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 283 | line | Return only default cosmetics - no hardcoded bypasses |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 284 | line | Admin/redeemed cosmetics come from the API via SupportersTabState |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 288-290 | block | * * Check if a cosmetic ID is a particle trail. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 295 | line | Check metadata type if available |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 301 | line | Fallback to string check |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 310-315 | block | * * Cosmetic metadata. * * @param tier 1 = Bronze, 2 = Silver, 3 = Gold * @param legacyId For resolving to Item/Block |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 319-321 | block | * * Cosmetic type enum. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 329 | line | Pet entity cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 330 | line | Custom head model cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 331 | line | Custom chest/torso model cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 332 | line | Custom leggings model cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticManager.java` | 333 | line | Custom boots model cosmetics |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 14-28 | block | * * Registry for parsing and resolving cosmetic IDs to Minecraft registry * entries. * * Cosmetic IDs are strings in format: * - "item:namespace:item_id" - Item cosmetic (e.g., * "item:minecraft:diamond_sword") * - "block:namespace:block_id... |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 32 | line | Cache for resolved items/blocks to avoid repeated lookups |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 37 | line | Custom cosmetic type definitions (can be extended) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 41 | line | Initialize custom types if needed |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 42 | line | Example: typeDefinitions.put("armor_set_1", new CosmeticType(...)); |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 49-55 | block | * * Parse a cosmetic ID string and return the type and identifier. * * @param cosmeticId Cosmetic ID string (e.g., * "buildscape:cosmatics/gear/diamond_sword") * @return Parsed cosmetic info, or null if invalid format |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 62 | line | Handle new format: buildscape:cosmatics/category/id |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 67 | line | e.g. "gear", "particle", "wings" |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 70 | line | Map new categories to internal types |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 73 | line | Default to item for gear |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 75 | line | Get legacy ID from CosmeticManager if possible for resolving |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 80 | line | Parse legacy ID to get namespace and id |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 94 | line | Fallback to legacy format: type:namespace:id |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 108-113 | block | * * Resolve a cosmetic ID to an Item. * * @param cosmeticId Cosmetic ID string * @return Item if found, null otherwise |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 120 | line | Check cache first |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 142-147 | block | * * Resolve a cosmetic ID to a Block. * * @param cosmeticId Cosmetic ID string * @return Block if found, null otherwise |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 154 | line | Check cache first |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 176-183 | block | * * Resolve a cosmetic ID to an ItemStack. * Handles both item and block cosmetics (blocks are converted to ItemStacks). * For particle trails, returns a placeholder item (nether star) for display. * * @param cosmeticId Cosmetic ID string *... |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 189 | line | Check if this is a custom HEAD cosmetic - these should NOT resolve to |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 190 | line | ItemStacks |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 191 | line | Custom head cosmetics like builder's hat use custom models, not ItemStack |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 192 | line | models |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 195 | line | Return null for custom head cosmetics - they use custom rendering |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 199 | line | Check cache first |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 209 | line | Check if it's a particle trail (use nether star as placeholder) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 213 | line | Pets use custom rendering and should not have an ItemStack representation |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 216 | line | For wings, try to resolve using legacyId first, then as item |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 228 | line | Placeholder for wings if item not found |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 233 | line | Try as item first |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 238 | line | Try as block |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 243 | line | Try as custom type |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 250 | line | Fallback: if cosmetic metadata has a legacyId, try resolving that |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 251 | line | Reuse the meta variable we already have from the HEAD check above |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 255 | line | legacyId might be block:... or item:... |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 270 | line | Cache result (even if null to avoid repeated lookups) |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 276-281 | block | * * Check if a cosmetic ID is valid and can be resolved. * * @param cosmeticId Cosmetic ID string * @return true if valid and resolvable |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 300 | line | NBT cosmetics are handled separately |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 307-310 | block | * * Clear all caches. * Useful for reloading or debugging. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 317-319 | block | * * Internal class to hold parsed cosmetic information. |
| `src/main/java/com/kingodogo/buildscape/cosmetics/CosmeticRegistry.java` | 323-326 | block | * * Interface for custom cosmetic types. * Extend this to create custom cosmetic type definitions. |
| `src/main/java/com/kingodogo/buildscape/data/ModBlockTagsProvider.java` | 23 | line | Vertical slab/stair tags are maintained as static tag JSON. |
| `src/main/java/com/kingodogo/buildscape/data/ModDataGen.java` | 24 | line | generator.addProvider(new ModItemModelProvider(generator, existingFileHelper)); |
| `src/main/java/com/kingodogo/buildscape/data/ModRecipeProvider.java` | 18 | line | Vertical slab/stair conversions are maintained as static recipe JSON. |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 351 | line | Save particle pattern and colors from synchronized data |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 378 | line | Handle ITEM tag from /give or /summon commands (custom NBT format) |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 391 | line | Invalid item ID, ignore |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 395 | line | Load particle pattern and colors from saved data |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 408 | line | Handle PATTERN tag from /give or /summon commands (custom NBT format) |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 414 | line | Handle COLORS tag from /give or /summon commands (custom NBT format) |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 453 | line | If the frame has an item, add it to the NBT so it persists when placed |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 463 | line | Preserve particle pattern from persistent data |
| `src/main/java/com/kingodogo/buildscape/entity/ColoredItemFrameEntity.java` | 470 | line | Preserve particle colors from persistent data |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 20 | line | 40 minutes in ticks |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 59 | line | Helper to get a random item from a list of suppliers |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 72 | line | 1. 1 Emerald - 8 white sand (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 76 | line | 2. 1 Emerald - 8 green sand (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 80 | line | 3. 1 Emerald - 8 red sand (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 84 | line | 4. 1 Emerald - 8 red tiles (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 88 | line | 5. 1 Emerald - 8 lime tiles (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 92 | line | 6. 1 Emerald - 4 snow overlay (4 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 96 | line | 7. 1 Emerald - 8 Dyed Festive Stockings (4 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 109 | line | 8. 2 Emerald - 16 Any Dyed String Lights (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 121 | line | 9. 1 Emerald - 8 Any Snowy Leaves (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 133 | line | 10. 2 Emerald - 16 Any Dyed Ornament (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 145 | line | 11. 1 Emerald - 8 Any Dyed Star (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 156 | line | 12. 1 Emerald - 8 Packed Icicle Blocks (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 160 | line | 13. 1 Emerald - 8 Any Glow Lights (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 167 | line | 14. 1 Emerald - 8 Festive Lamp (8 trades) |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 171 | line | Shuffle and pick exactly 6 trades |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 177 | line | Rare trade: 5 Diamonds for 1 Frost Rose (max 5 trades), extremely rare, |
| `src/main/java/com/kingodogo/buildscape/entity/FestiveWanderingHomemakerEntity.java` | 178 | line | slightly more common on 25th Dec |
| `src/main/java/com/kingodogo/buildscape/entity/ModEntities.java` | 77 | line | Small invisible entity |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 11-13 | block | * * An invisible entity that a player can "ride" to sit on blocks like log slabs. |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 28 | line | No data to sync |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 33 | line | No data to save |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 38 | line | No data to save |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 45 | line | Remove the seat if it has no passengers (e.g. player dismounted) |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 55 | line | Adjust the height of the player relative to the entity |
| `src/main/java/com/kingodogo/buildscape/entity/SeatEntity.java` | 69-71 | block | * * Helper to spawn a seat and make the player sit on it. |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 18 | line | 40 minutes in ticks |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 92 | line | New Vegetation/Flora trades |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 102 | line | Sculk Catalyst trade |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 105 | line | Muff Block trade |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 108 | line | Mist Bottle trade |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 111 | line | Confetti trade |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 114 | line | Resin Clump & Resin Block trades |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 118 | line | Creaking Heart trade |
| `src/main/java/com/kingodogo/buildscape/entity/WanderingHomemakerEntity.java` | 126 | line | Rare trade: 1 in 5000 chance to offer 1 scroll for 12 diamonds |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 34-39 | block | * * Handles custom advancement triggers, progress counters, and reward events. * * TODO: Physical Trophy blocks/items to be awarded once registered in Buildscape. * TODO: Special Tools/Templates (e.g. Golden Shears) to be awarded once regis... |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 121 | line | If advancement was revoked after stat accumulated, re-snapshot baseline to count fresh from current action |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 144 | line | Ashenking Pillar Item & Trophy Rewards |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 179 | line | Check if placed block belongs to Buildscape |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 195 | line | Stained Brick |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 200 | line | Hollow Logs |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 205 | line | Awarded |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 209 | line | Icicles |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 214 | line | Awarded |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 218 | line | Ornaments |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 227 | line | String Lights |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 237 | line | Stars |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 247 | line | Snowy Leaves |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 256 | line | Frosty Rose ("Let it Snow" - Place 5 Frosty Rose together) |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 271 | line | Cascade Block ("Let it Cascade" - Place 1 Cascade Block) |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 277 | line | Smoke Vent ("Let It Out" - Place 5 Smoke Vent) |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 282 | line | Granted |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 286 | line | Muff Block |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 294 | line | Bolts ("Are you Nuts" - Place 20 Bolts) |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 299 | line | Granted |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 303 | line | Pillar Placement & Height Tracking |
| `src/main/java/com/kingodogo/buildscape/event/AdvancementEvents.java` | 322 | line | Check vertical pillar height (any pillar type) for 50-block high pillar |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 33 | line | Cancel standard block breaking |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 44 | line | Sneak + Left-click to clear biome |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 47 | line | Normal Left-click to set Position 2 |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 65 | line | Send clear packet to server |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 111 | line | Spawn vertical edges |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 117 | line | Spawn horizontal edges - X |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 123 | line | Spawn horizontal edges - Z |
| `src/main/java/com/kingodogo/buildscape/event/BiomeBrushHandler.java` | 129 | line | Highlight corners/positions |
| `src/main/java/com/kingodogo/buildscape/event/FestiveGlintAnvilHandler.java` | 25 | line | Can only be applied to already enchanted items |
| `src/main/java/com/kingodogo/buildscape/event/FestiveGlintAnvilHandler.java` | 34 | line | Handle custom name if modified in anvil text field |
| `src/main/java/com/kingodogo/buildscape/event/FrostRoseDropHandler.java` | 41 | line | Drop Frost Rose item at the Snow Golem's death position |
| `src/main/java/com/kingodogo/buildscape/event/FrostRoseDropHandler.java` | 52 | line | Track this death position to intercept Wither Rose (both block and item) |
| `src/main/java/com/kingodogo/buildscape/event/FrostRoseDropHandler.java` | 56 | line | Intercept Wither Rose ITEM ENTITY spawning near a Snow Golem death position |
| `src/main/java/com/kingodogo/buildscape/event/FrostRoseDropHandler.java` | 76 | line | Intercept Wither Rose BLOCK placed at a Snow Golem death position |
| `src/main/java/com/kingodogo/buildscape/event/FrostRoseDropHandler.java` | 86 | line | Check death position and one above for Wither Rose block |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 31-36 | block | * * TOGGLE: * - true: Uses the original forced Pose.SWIMMING behavior. * - false (Default Test Environment): Uses the new dedicated custom crawl state (0.6x0.6 cavity hitbox) * without forcing Pose.SWIMMING on the player. |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 83 | line | Option A: Original forced Pose.SWIMMING handler |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 92 | line | Option B: New Custom Crawl State for hollow logs & pipes |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 98 | line | Maintain visual crawling pose naturally while inside cavity without forcedPose |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 160-162 | block | * * Checks if the player's bounding box is physically inside a horizontal hollow log or pipe cavity (12x12 pixels gap). |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 250-252 | block | * * Checks if the player is actively sneaking directly at an unobstructed horizontal opening (12x12 gap entrance). |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 274 | line | Player must be at ground level facing the cavity opening, never standing on top of the roof |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 321 | line | Check West entrance |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 327 | line | Check East entrance |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 333 | line | Check North entrance |
| `src/main/java/com/kingodogo/buildscape/event/HollowLogCrawlHandler.java` | 339 | line | Check South entrance |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 83 | line | Always register if it has custom data, otherwise just ensure it's tracked |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 97 | line | Get the frame ID and remove from manager |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 102 | line | Clear client caches for this entity |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 112 | line | Support both vanilla ItemFrame and our ColoredItemFrameEntity |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 118 | line | ColoredItemFrameEntity extends HangingEntity, not ItemFrame |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 119 | line | So we need to handle it separately |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 136 | line | Remove dyeing interaction as requested. |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 137 | line | We only allow adding particle colors. |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 258 | line | Handle vanilla ItemFrame |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 281 | line | Handle ColoredItemFrameEntity |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 418 | line | Snowflake pattern: Reverse of beam pattern |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 421 | line | Fixed 2 blocks far |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 448 | line | Determine particle type and color queuing based on pattern |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 454 | line | Only queue color for non-snowflake particles |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 485 | line | 1. Check direct NBT on the entity (immediate response) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 497 | line | 2. Check synced MANAGER data (fallback for persistence) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 586 | line | 1. Check direct NBT on the entity (immediate response on rejoin) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 596 | line | 2. Fallback to PillarIdManager data synced from server |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 625 | line | Generate ID if missing during pattern set |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 740 | line | Fallback to position-based identification on client |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 745 | line | 1. Exact match (pos + direction) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 749 | line | 2. Fallback: If exact match fails, check if we're waiting for entity sync (dir might be wrong) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 751 | line | Try to find any frame ID at this exact position if there's no direction-specific index yet |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 752 | line | or if the direction is currently default (SOUTH) but the frame is actually on a side. |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 753 | line | NOTE: We only do this on the client during initial identification. |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 755 | line | ID for position without direction might return a pillar, so we must verify it's a frame ID if found |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 784 | line | Use a more unique ID including coordinate hash to avoid session collisions |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 788-791 | block | * * Generate a frame ID with color code for colored item frames. * Format: I-F[COLOR]nnnn where COLOR is like W, LB, R, etc. and nnnn is 4 hex digits |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 798-800 | block | * * Convert color name to short code. |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 856 | line | ========== ColoredItemFrameEntity Support ========== |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 871 | line | Remove re-dyeing interaction as requested. |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 872 | line | We only allow adding particle colors. |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 964 | line | Fallback: Check synchronized data for our custom frame |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 971 | line | Fallback to PillarIdManager data synced from server |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 991 | line | Also check PATTERN tag from NBT (for /give commands) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1020 | line | 1. Check direct NBT on the entity (immediate response) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1032 | line | 2. Check synchronized data for our custom frame |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1042 | line | 3. Check synced MANAGER data (fallback for persistence) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1101 | line | Fallback to position-based identification on client |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1106 | line | 1. Exact match (pos + direction) |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1110 | line | 2. Fallback: If exact match fails, check if we're waiting for entity sync |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1133 | line | Generate ID with color code for colored frames |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1293 | line | Snowflake pattern: Reverse of beam pattern |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1296 | line | Fixed 2 blocks far |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1323 | line | Determine particle type and color queuing based on pattern |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1326 | line | Use no-gravity version |
| `src/main/java/com/kingodogo/buildscape/event/ItemFrameParticleHandler.java` | 1329 | line | Only queue color for non-snowflake particles |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 34 | line | Case 1: Mud was placed - check for dripstone below |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 39 | line | Case 2: Pointed dripstone placed - check for mud two blocks above |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 48 | line | Case 3: Block placed between existing mud and dripstone |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 61 | line | Avoid duplicate tracking |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 72 | line | Requires: non-air block below mud, pointed dripstone facing down below that |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 76 | line | 1-2 seconds (20-40 ticks) |
| `src/main/java/com/kingodogo/buildscape/event/MudToClayHandler.java` | 93 | line | Verify mud is still there before converting |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 14-17 | block | * * Handles syncing of pillar IDs to the client when they join the server. * This ensures the client side has the latest data immediately. |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 27 | line | On join, send all pillar data from server to the joining client |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 30 | line | Ensure manager is loaded on server side |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 35 | line | Sync colors from NBT before sending to ensure freshest data |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 41 | line | Get all pillar data and send to client |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 50 | line | Also sync gamerules to the joining player so their GUI is correct from the start |
| `src/main/java/com/kingodogo/buildscape/event/PillarIdJoinSyncHandler.java` | 62 | line | Sync Wandering Homemaker cooldown |
| `src/main/java/com/kingodogo/buildscape/event/StrawBedHandler.java` | 25 | line | Get the position where the player was sleeping |
| `src/main/java/com/kingodogo/buildscape/event/StrawBedHandler.java` | 31 | line | Check if the block is a Straw Bed |
| `src/main/java/com/kingodogo/buildscape/event/StrawBedHandler.java` | 33 | line | Destroy the straw bed block (consumable bed) on wake up |
| `src/main/java/com/kingodogo/buildscape/event/TagTooltipHandler.java` | 40-42 | block | * * Checks if the player has enabled Shulker Preview in Player Rules. |
| `src/main/java/com/kingodogo/buildscape/event/TagTooltipHandler.java` | 62 | line | Festive stocking tooltip handling |
| `src/main/java/com/kingodogo/buildscape/event/TagTooltipHandler.java` | 84 | line | If player disabled Shulker Preview in Player Rules, do not touch or modify tooltips at all. |
| `src/main/java/com/kingodogo/buildscape/event/TagTooltipHandler.java` | 85 | line | This leaves vanilla, Shulker Plus (Iskallia), Tweakeroo, etc. completely untouched. |
| `src/main/java/com/kingodogo/buildscape/event/WanderingHomemakerSpawningHandler.java` | 105 | line | Cooldown active |
| `src/main/java/com/kingodogo/buildscape/event/WanderingHomemakerSpawningHandler.java` | 108 | line | Despawn old homemaker if it's currently loaded |
| `src/main/java/com/kingodogo/buildscape/event/WanderingHomemakerSpawningHandler.java` | 137 | line | 2 to 3 blocks away |
| `src/main/java/com/kingodogo/buildscape/event/WanderingHomemakerSpawningHandler.java` | 154 | line | 30 minutes in milliseconds |
| `src/main/java/com/kingodogo/buildscape/firework/CustomFireworkRenderer.java` | 44 | line | Rotate point horizontally around Y-axis by shooter's yaw |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 12 | line | Color Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 13 | line | Creamy white |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 14 | line | Pure white |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 15 | line | Chocolate brown |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 16 | line | Warm caramel/orange brown |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 17 | line | Strawberry red |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 18 | line | Frosting pink |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 19 | line | Gold candle |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 20 | line | White candle |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 21 | line | Bright yellow |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 22 | line | Orange flame |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 37 | line | Tier 1: Bottom Layer (Largest rectangular voxel block: x in [-10, 10], z in [-10, 10], y in [-7, -2]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 50 | line | Frosting trim on top rim of tier 1 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 53 | line | Cake corners |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 56 | line | Cake base body |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 65 | line | Tier 2: Middle Layer (Medium voxel block: x in [-7, 7], z in [-7, 7], y in [-2, 3]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 77 | line | Frosting rim for middle tier |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 88 | line | Tier 3: Top Layer (Smallest voxel block: x in [-4, 4], z in [-4, 4], y in [3, 7]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 105 | line | Candles (3 vertical glowing stalks on top of tier 3) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 106 | line | Center Candle (Gold) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 108 | line | Left Candle (White/Gold) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 110 | line | Right Candle (White/Gold) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CakeFireworkShape.java` | 123 | line | Candle Flame (Secondary glowing particles above top of candle) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 12 | line | Pure white for permanently white stripes |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 28 | line | 1. Vertical Shaft (y in [-12, 4], shaft centered at x = -3, z = 0) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 32 | line | Determine stripe index along shaft |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 36 | line | Add 3D shaft cross section (thickness in X and Z) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 40 | line | White stripes ALWAYS stay pure white (0xFFFFFF) regardless of dye! |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 43 | line | Colored stripes use item dye color (colorOverride = -1) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 50 | line | 2. Blocky Stepped Hook (Arching from x = -3, y = 4 up to top y = 10, curving right to x = 5, y = 6) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 51 | line | Hook curve parameter angle theta from 0 to PI |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 57 | line | Stepped 3D blocky curve |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 67 | line | White stripes ALWAYS stay pure white (0xFFFFFF)! |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 70 | line | Colored stripes use item dye color! |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CandyCaneFireworkShape.java` | 77 | line | 3. Festive Sparkles around tip and hook |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 12 | line | Color Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 13 | line | Pure white star center |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 14 | line | Bright gold star |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 15 | line | Bright yellow star |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 16 | line | Dark wood trunk |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 17 | line | Warm brown trunk |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 18 | line | Dark green interior foliage |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 19 | line | Forest green foliage |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 20 | line | Bright green outer foliage tips |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 22 | line | Christmas Lights Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 24 | line | Red |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 25 | line | Green |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 26 | line | Blue |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 27 | line | Yellow |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 28 | line | Cyan |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 29 | line | Magenta |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 30 | line | Orange |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 33 | line | Ornaments Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 35 | line | Bright red bauble |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 36 | line | Gold bauble |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 37 | line | Royal blue bauble |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 38 | line | Purple bauble |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 39 | line | Cyan bauble |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 55 | line | 1. Trunk Base (x in [-3, 3], z in [-3, 3], y in [-14, -8]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 65 | line | 2. 7 Tiered Foliage Layers (y in [-8, 12]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 66 | line | Layer definitions: {yStart, yEnd, maxRadiusX, maxRadiusZ} |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 68 | line | Tier 1: Lowest, largest branches |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 69 | line | Tier 2 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 70 | line | Tier 3 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 71 | line | Tier 4 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 72 | line | Tier 5 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 73 | line | Tier 6 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 74 | line | Tier 7: Top narrow cone |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 96 | line | Leaf point color based on depth |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 99 | line | Outer tip highlight |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 103 | line | Rear/interior shadow |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 107 | line | 3D interior depth point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 110 | line | Add Christmas String Lights on outer foliage edges |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 117 | line | Add Ornaments hanging from branch tips |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 128 | line | 3. Large Top Star (at y = 13 to 17) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 130 | line | Star Center Core |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 135 | line | 5 Star Points (Vertical, Horizontal, & Top Spire) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 138 | line | Top point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 139 | line | Bottom point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 140 | line | Right point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 141 | line | Left point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 142 | line | Front point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 143 | line | Back point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/ChristmasTreeFireworkShape.java` | 146 | line | Extra Top Sparkles |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 12 | line | Color Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 13 | line | Royal gold |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 14 | line | Bright yellow-gold |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 15 | line | Dark gold/amber |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 16 | line | Bright red gem |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 17 | line | Deep blue gem |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 18 | line | Royal purple gem |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 19 | line | Bright cyan gem |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 20 | line | Bright green gem |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 35 | line | Crown Base Band (Oval ring in X/Z plane from y = -6 to y = -2) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 46 | line | Top rim highlight of base band |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 48 | line | Bottom rim shadow of base band |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 53 | line | Add base band point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 58 | line | Jewels on Base Band |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 59 | line | Center Front Gem (Ruby) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 61 | line | Front Left Gem (Diamond Cyan) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 63 | line | Front Right Gem (Sapphire Blue) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 65 | line | Side Left Gem (Amethyst) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 67 | line | Side Right Gem (Emerald) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 70 | line | 5 Crown Spikes / Points (Rising from y = -2 upwards) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 71 | line | Point 1: Center Spire (Tallest, x = 0, y up to 11) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 74 | line | Point 2: Inner Left Spire (Medium-Tall, x = -6, y up to 8) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 77 | line | Point 3: Inner Right Spire (Medium-Tall, x = 6, y up to 8) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 80 | line | Point 4: Outer Left Spire (Medium, x = -11, y up to 6) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 83 | line | Point 5: Outer Right Spire (Medium, x = 11, y up to 6) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 86 | line | Arching Crown Ribs (3D interior caps joining front/back to top center) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 107 | line | Front layer point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 113 | line | Back layer for 3D depth |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/CrownFireworkShape.java` | 117 | line | Tip Gem / Star at top of spire |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 12 | line | Flame Gradient Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 13 | line | Hottest core & highlights |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 14 | line | Bright yellow |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 15 | line | Warm gold |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 16 | line | Vibrant flame orange |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 17 | line | Fiery red tips |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 18 | line | Deep shadow red |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 26 | line | Massive Phoenix scale (2.5 - 4x size of STAR) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 33 | line | 1. Slender Body & Glowing Chest Core (y in [-4, 8]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 34 | line | Hot White/Yellow Chest Core (y in [3, 8]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 48 | line | Tapering Lower Body (y in [-4, 3]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 56 | line | Depth shadow point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 63 | line | 2. Graceful Curved Neck (y in [8, 14]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 77 | line | 3. Small Refined Phoenix Head & Pointed Beak & Eye (y in [14, 16]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 79 | line | Head Sphere Core |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 88 | line | Pointed Beak (extending forward/upward) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 93 | line | Tiny Glowing Eye (White glints) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 97 | line | 5 Flame Crest Feathers (sweeping backward and upward from behind head) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 99 | line | Center tallest crest |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 100 | line | Inner Left |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 101 | line | Inner Right |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 102 | line | Outer Left |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 103 | line | Outer Right |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 120 | line | 4. Enormous Sweeping Wings (2 Symmetric Wings, 8 Major Feather Groups per wing) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 121 | line | We generate Left Wing (side = -1) and Right Wing (side = 1) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 123 | line | Feather Group definitions: {startX, startY, endX, endY, ZOffset} |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 125 | line | Group 1: Shoulder / Top Covert (Shortest, innermost) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 127 | line | Group 2: Upper Arch |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 129 | line | Group 3: Peak Wing Spire |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 131 | line | Group 4: Primary Outer Feather 1 (LONGEST, sweeping to far tip) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 133 | line | Group 5: Primary Outer Feather 2 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 135 | line | Group 6: Primary Outer Feather 3 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 137 | line | Group 7: Secondary Flight Feather 1 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 139 | line | Group 8: Secondary Flight Feather 2 (Lowest, innermost) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 148 | line | Quadratic Bezier curve along feather shaft |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 155 | line | Flame gradient color along feather length |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 169 | line | Main feather shaft point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 172 | line | Feather width (secondary feather detail points) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 176 | line | Rear depth layer (Dark Red) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 180 | line | Pointed Feather Tip Sparkle |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 187 | line | 5. Long Flowing Flame Tail (7 Individual Strands curving downward & outward) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 189 | line | Strand 0: Center (Longest, y down to -22) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 191 | line | Strand 1: Inner Left |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 193 | line | Strand 2: Inner Right |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 195 | line | Strand 3: Mid Left |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 197 | line | Strand 4: Mid Right |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 199 | line | Strand 5: Outer Left |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 201 | line | Strand 6: Outer Right |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PhoenixFireworkShape.java` | 231 | line | Tail tip sparkles |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 12 | line | Decorative Highlight Colors |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 13 | line | Gold accent |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 14 | line | White highlight |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 15 | line | Bright knot center |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 30 | line | One Single Large 3D Gift Box: |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 31 | line | Width: x in [-8, 8], Height: y in [-8, 6], Depth: z in [-8, 8] |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 47 | line | Outer faces of the 3D gift box |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 55 | line | Ribbon point (uses gold/white accents or item dye accent) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 59 | line | Gift Box wrapping (colorOverride = -1 so it dynamically uses item dye color!) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 67 | line | Large 3D Ribbon Bow on top (at y = 7 to 12) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 70 | line | Center Knot |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 74 | line | Left Bow Loop (x from 0 to -6, y loops up to bowBaseY + 3.5, z thickness [-2, 2]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 77 | line | Right Bow Loop (x from 0 to +6, y loops up to bowBaseY + 3.5, z thickness [-2, 2]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 80 | line | Hanging Ribbon Tails |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 87 | line | Festive Sparkles around corners and bow |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/PresentsFireworkShape.java` | 100 | line | Loop 3D depth in Z |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 12 | line | Icy Winter Color Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 13 | line | Pure white center & crystal tips |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 14 | line | Bright icy white primary shaft |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 15 | line | Soft light blue branches |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 16 | line | Vibrant ice blue details |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 17 | line | Subtle Z-depth shadow blue |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 25 | line | Optimized scale for clear 6-arm expansion and readability |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 32 | line | 1. Distinctive Central Crystal Anchor |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 37 | line | Central 6-point diamond ring |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 51 | line | 2. Six Primary Arms generated with perfect 6-fold radial symmetry |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 56 | line | A) Central Shaft of the Arm (yArm from 3.5 to 18.0) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 64 | line | 3D Depth Layering (subtle z-offset) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 71 | line | B) Pair 1: Inner Large Secondary Branches (at yArm = 6.5) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 74 | line | C) Pair 2: Middle Medium Secondary Branches (at yArm = 11.5) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 77 | line | D) Pair 3: Outer Small Secondary Branches (at yArm = 15.0) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 80 | line | E) Crystalline Spear Tip (at yArm = 18.0) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 85 | line | Side prongs of spear tip |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 93 | line | Outer Sparkle Tip |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 103 | line | 45 degree diagonal branch |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 110 | line | Transform arm coordinates to world coordinates using rotation matrix |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/SnowflakeFireworkShape.java` | 117 | line | Branch tip crystal detail point |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 12 | line | Color Palette |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 13 | line | Bright gold |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 14 | line | Bright yellow-white gold |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 15 | line | Dark amber gold |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 16 | line | Bronze pedestal base |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 17 | line | Bright cyan central gem |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 18 | line | White sparkle stars |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 33 | line | 1. Pedestal Base (Large rectangular/tiered base at bottom y = -11 to y = -7) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 34 | line | Bottom Slab (x in [-8, 8], z in [-5, 5], y = -11) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 41 | line | Middle Step (x in [-6, 6], z in [-4, 4], y = -9 to -7) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 50 | line | 2. Narrow Stem (y = -7 to y = -2, stem width x in [-2, 2], z in [-2, 2]) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 52 | line | Slight waist pinch |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 60 | line | 3. Cup Base / Node (Expanding upward from y = -2 to y = 0) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 70 | line | 4. Main Trophy Cup Body (Bowl curving outward from y = 0 to y = 8) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 73 | line | Widens to ~10 at top |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 74 | line | Widens to ~7 at top |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 82 | line | Top Rim |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 84 | line | Rear depth |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 92 | line | Top Rim Highlight Ring (y = 8) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 101 | line | 5. Left & Right Curved Handles |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 102 | line | Left Handle (Loops outward to x = -15, y from 1 to 7) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 104 | line | Right Handle (Loops outward to x = +15, y from 1 to 7) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 107 | line | 6. Central Emblem / Jewel (Cyan Diamond on front of cup at y = 4, z = 4.5) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 114 | line | 7. Sparkle Stars (Secondary decorative glints around handles and top rim) |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 123 | line | Curve parameter t from 0 to 1 |
| `src/main/java/com/kingodogo/buildscape/firework/shapes/TrophyFireworkShape.java` | 129 | line | Handle 3D thickness (front and back points) |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 103 | line | Netheite items are immune to fire and explosions. But allow bypassInvul (void, creative-mode kill) |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 112 | line | Biome NBT helpers |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 136 | line | Position NBT helpers |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 180 | line | 0. Check if brush is broken |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 187 | line | 1. Sneak + Right-click to apply biome |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 228 | line | Caching variables for fast lookup |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 254 | line | Retrieve chunk (cached) |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 279 | line | Check durability |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 290 | line | Retrieve section (cached) |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 309 | line | Check if the quart is different from the last set quart in this container |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 320 | line | Handle durability decrease |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 338 | line | Sync all modified chunks |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 344 | line | Play sound |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 347 | line | Display feedback |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 352 | line | Clear positions after application |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 362 | line | 2. Normal Right-click (Non-sneaking) |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 369 | line | Capture Biome |
| `src/main/java/com/kingodogo/buildscape/item/BiomeBrushItem.java` | 385 | line | Set Position 1 |
| `src/main/java/com/kingodogo/buildscape/item/BottleOfMistItem.java` | 28 | line | Spawn a burst of cascade particles that last ~2 seconds (40 ticks) |
| `src/main/java/com/kingodogo/buildscape/item/BottleOfMistItem.java` | 33 | line | Look direction for forward offset |
| `src/main/java/com/kingodogo/buildscape/item/ConfettiItem.java` | 31 | line | Play sounds and spawn particles on server-side |
| `src/main/java/com/kingodogo/buildscape/item/ConfettiItem.java` | 63 | line | Explosive burst scaled by burstLevel |
| `src/main/java/com/kingodogo/buildscape/item/ExperienceBucketItem.java` | 40 | line | If sneaking, bypass fluid block placing and drink directly |
| `src/main/java/com/kingodogo/buildscape/item/ExperienceBucketItem.java` | 46 | line | Try placement first |
| `src/main/java/com/kingodogo/buildscape/item/ExperienceBucketItem.java` | 60 | line | Grant 25-30 XP (average ~27.5 XP) |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 52 | line | 1. Gather ALL potential items for this tab |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 305 | line | --- COPPER EXPANSION --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 325 | line | Chiseled Copper |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 335 | line | Slit Copper |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 372 | line | Copper Grates |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 382 | line | Copper Bulbs |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 392 | line | Copper Doors |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 402 | line | Copper Trapdoors |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 412 | line | Copper Bars |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 435 | line | Copper Chests |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 445 | line | Copper Buttons |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 455 | line | Copper Pressure Plates |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 473 | line | Chains |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 487 | line | Bolts |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1023 | line | Tiles |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1105 | line | Colored Stained Bricks |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1267 | line | Colored Concrete |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1333 | line | Colored Polished Concrete |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1415 | line | Terracotta |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1468 | line | Colored Wool |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1469 | line | White |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1483 | line | Light Gray |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1497 | line | Gray |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1511 | line | Black |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1525 | line | Brown |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1539 | line | Red |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1553 | line | Orange |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1567 | line | Yellow |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1581 | line | Lime |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1595 | line | Green |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1609 | line | Cyan |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1623 | line | Light Blue |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1637 | line | Blue |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1651 | line | Purple |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1665 | line | Magenta |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1679 | line | Pink |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1693 | line | Colored Cushions |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1711 | line | Colored Wallpapers |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1777 | line | Spools |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1797 | line | Dye Sacks |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1816 | line | Big Books |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1836 | line | Sandstone Variants |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1926 | line | --- GLASS STAIRS & SLABS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 1982 | line | --- FACTORY GLASS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2064 | line | --- MOSAIC GLASS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2146 | line | --- GLAZED GLASS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2228 | line | Ornaments |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2248 | line | Big Ornament Template & Big Ornaments |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2269 | line | Colored Stars |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2311 | line | --- GLASS JARS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2350 | line | --- FROGLIGHTS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2368 | line | Colored Big Candle |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2389 | line | Colored Item Frames |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2459 | line | --- PILLARS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2510 | line | Custom Firework Stars (Cake, Crown, Trophy) |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2555 | line | Custom Base Firework Rockets (Cake, Crown, Trophy, Christmas Tree, Presents, Candy Cane, Phoenix, Snowflake) |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2673 | line | --- PALE OAK WOODSET --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2705 | line | --- CHERRY WOODSET --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2737 | line | --- PALE MOSS & HANGING MOSS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2747 | line | --- RESIN & CREAKING HEART --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2761 | line | Poplar Wood Set |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2965 | line | --- VANILLA WOOD LOG/WOOD VARIANTS (By Wood Species) --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2966 | line | OAK |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 2986 | line | SPRUCE |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3006 | line | BIRCH |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3026 | line | JUNGLE |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3046 | line | ACACIA |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3066 | line | DARK OAK |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3086 | line | CRIMSON |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3104 | line | WARPED |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3121 | line | --- CARDBOARD FAMILY --- |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3263 | line | Bush -> Red bush -> Firefly bush |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3268 | line | Dry grass -> Tall dry grass |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3272 | line | Frost rose |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3275 | line | Snowy foliage |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3282 | line | Snowy leaves |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3298 | line | Leaf layers |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3314 | line | Leaf hedge |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3330 | line | Snowy leaf layers |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3346 | line | Snowy leaf hedge |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3362 | line | Rose vines |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3368 | line | Monets |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3376 | line | Spore blossom |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3383 | line | Petals |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3390 | line | Wildflowers |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3393 | line | Clover |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3396 | line | Leaf litter |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3399 | line | Closed eyeblossom -> Open eyeblossom |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3403 | line | Golden dandelion & Cactus flower |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3407 | line | Mushroom Shelves |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3411 | line | Straw Bed & Hay Bale |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3417 | line | Stone |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3421 | line | Quartz |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3433 | line | Calcite |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3444 | line | Tuff Variants |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3462 | line | Bit Tuff Variants |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3476 | line | Sulfur |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3496 | line | Cinnabar |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3514 | line | Dripstone |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3520 | line | Amethyst |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3525 | line | Basalt |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3534 | line | Prismarine |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3538 | line | Sandstone |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3548 | line | End Stone & Purpur |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3556 | line | Obsidian |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3564 | line | Bedrock |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3570 | line | Snow & Snowy Grass |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3585 | line | Icicles |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3590 | line | Dirt, Podzol, Mycelium |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3601 | line | Moss |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3608 | line | Colored Moss |
| `src/main/java/com/kingodogo/buildscape/item/ModCreativeModeTab.java` | 3631 | line | --- SCULK FAMILY --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 2853 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 2854 | line | Trapped Decorated Pot items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 2855 | line | ========================================================================= |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 5582 | line | template block, hidden from creative tab |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6274 | line | Steel Block Variants |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6290 | line | Polished Steel Variants |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6306 | line | Pressed Steel Variants |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6322 | line | Cut Steel Variants |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6338 | line | Caution Blocks |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6404 | line | Caution Slabs |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6470 | line | Caution Stairs |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6536 | line | Factory Glass Blocks |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6617 | line | Factory Glass Panes |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6698 | line | Stained Bricks |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6779 | line | Stained Brick Tiles |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 6844 | line | Stained Brick Tiles Slabs, Stairs, Walls, and Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 7103 | line | Stained Bricks Slabs |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 7184 | line | Stained Bricks Stairs |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 7265 | line | Stained Bricks Walls |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 7346 | line | Colored Item Frames |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 7763 | line | Colored Redstone Lamps |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 8516 | line | Log Slab Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 8583 | line | Static vertical slab and stair block items generated from BuildScape horizontal variants. |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9072 | line | End generated vertical variants. |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9074 | line | Spool Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9094 | line | Dye Sack Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9113 | line | Colored Moss Block Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9133 | line | Tuff Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9148 | line | Wool Slab, Stairs, and Wall Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9198 | line | Poplar Wood Set Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9225 | line | Poplar Leaves Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9230 | line | Cinnabar Set Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9245 | line | Sulfur Set Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9262 | line | Vanilla normal slab prerequisite block items. |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9329 | line | Vanilla family vertical slab block items. |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9377 | line | Extended vanilla slab and stair coverage block items. |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9557 | line | Vanilla gap slab and stair coverage block items. |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9725 | line | Wallpaper Slabs and Vertical Slabs |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9855 | line | Wallpaper Flat Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9890 | line | End new main vertical variant block items. |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9892 | line | Cushions |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9926 | line | Straw Bed (stackable up to 16) |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9930 | line | Big Books |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9968 | line | --- PALE OAK ITEMS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 9995 | line | --- CHERRY ITEMS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10022 | line | --- PALE MOSS ITEMS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10032 | line | --- RESIN & CREAKING HEART --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10046 | line | --- PLANTS & FLOWERS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10059 | line | --- FROGLIGHTS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10077 | line | --- SCULK ITEMS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10088 | line | --- COPPER LIGHTING & TORCH --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10091 | line | --- COPPER RODS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10101 | line | --- COPPER LANTERNS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10111 | line | --- COPPER EXPANSION ITEMS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10216 | line | Copper Buttons |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10226 | line | Copper Bolts |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10236 | line | Copper Pressure Plates |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10328 | line | Glass Jars |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10406 | line | --- LAYERED WOOLS --- |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 10456 | line | Layered Wool Slabs, Stairs, Walls, Carpets, Layers & Vertical Slabs Items |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 11551 | line | CARDBOARD ITEMS BEGIN |
| `src/main/java/com/kingodogo/buildscape/item/ModItems.java` | 12112 | line | CARDBOARD ITEMS END |
| `src/main/java/com/kingodogo/buildscape/item/StringlightFrameItem.java` | 14-16 | block | * * Cosmetic attachment item that can be applied to signs. |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 80 | line | 1. Consume from input slot first |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 86 | line | 2. Consume from player inventory |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 101 | line | 3. Give output stacks to the player |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 112 | line | 4. Award recipe |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 115 | line | 5. Refresh the recipe list |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 118 | line | 6. Broadcast changes to container |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 137 | line | Pass 0: Stack into non-empty slots with matching filter |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 147 | line | Pass 1: Stack into non-empty unfiltered slots |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 157 | line | Pass 2: Insert into empty slots with matching filter |
| `src/main/java/com/kingodogo/buildscape/mixin/AbstractContainerMenuMixin.java` | 167 | line | Pass 3: Insert into empty unfiltered slots |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 51 | line | Steel blocks cycle (One More Block - 100 blocks, non-flaming full cubes) |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 69 | line | Tile blocks cycle (Okay, One More - 1,000 blocks, full cubes) |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 87 | line | Ashpen blocks cycle (Actually, One Last - 10,000 blocks, full cubes) |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 109 | line | Bit Copper blocks cycle (One Last one, i promise - 100,000 blocks, full cubes) |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 123 | line | Spool cycle |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 136 | line | Wallpaper cycle |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 148 | line | Froglight cycle |
| `src/main/java/com/kingodogo/buildscape/mixin/AdvancementWidgetMixin.java` | 156 | line | Christmas cycle |
| `src/main/java/com/kingodogo/buildscape/mixin/BaseCoralPlantTypeBlockMixin.java` | 18-22 | block | * * Allow coral plants and fans to be placed on mud blocks. * MudBlock's collision shape is 15 units tall, so isFaceSturdy(UP) returns false * for mud — we override canSurvive to explicitly permit placement on mud. |
| `src/main/java/com/kingodogo/buildscape/mixin/BaseCoralPlantTypeBlockMixin.java` | 30-32 | block | * * Treat adjacent mud as water so coral does not dry out when placed on mud. |
| `src/main/java/com/kingodogo/buildscape/mixin/BeaconBlockEntityMixin.java` | 24 | block | * Sentinel for "nothing cuts this beam short". |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeBlockModelMixin.java` | 22-27 | block | * * Memoizes material resolution for Buildscape model instances during one * resource reload. New model objects naturally invalidate the cache. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeBlockStateCacheMixin.java` | 11-16 | block | * * Collects Buildscape block-state cache work during the Forge block-registry * bake. Other mods and vanilla states are never intercepted. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeForgeRegistryMixin.java` | 14-19 | block | * * Defines the narrow registry-bake window used to parallelize Buildscape block * state caches without changing Forge validation or other mods' registries. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeMixinPlugin.java` | 10-17 | block | * * Prevents Buildscape's cache hooks from competing with the standalone * LaunchFaster mod when both jars are installed. Model loading and baking * remain available because they can yield to LaunchFaster at runtime based on * its individua... |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeModelBakeryMixin.java` | 45-50 | block | * * Accelerates Buildscape-owned model work while leaving every other namespace * on the standard Minecraft and Forge model pipeline. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeModelBakeryMixin.java` | 107 | line | Forge geometry loaders may execute mod code while parsing. Keep those |
| `src/main/java/com/kingodogo/buildscape/mixin/BuildscapeModelBakeryMixin.java` | 108 | line | models on Forge's normal sequential path. |
| `src/main/java/com/kingodogo/buildscape/mixin/ComposterBlockMixin.java` | 55 | line | Pass to allow plant placement on top of the block |
| `src/main/java/com/kingodogo/buildscape/mixin/ComposterBlockMixin.java` | 86 | line | If it's a grass block, check if block above is snow to place it as snowy grass block |
| `src/main/java/com/kingodogo/buildscape/mixin/ComposterBlockMixin.java` | 96 | line | Play placement sound |
| `src/main/java/com/kingodogo/buildscape/mixin/CreativeModeTabMixin.java` | 19-23 | block | * * Keeps Buildscape's vanilla vertical slabs beside their horizontal variants. * * @author hoyin1600p |
| `src/main/java/com/kingodogo/buildscape/mixin/EmbeddiumPipeSpillMixin.java` | 18 | block | * Optional, outlet-only path; ordinary Embeddium water remains untouched. Author: HoYin1600p. |
| `src/main/java/com/kingodogo/buildscape/mixin/EntityMixin.java` | 35 | line | Use Accessor to safely call methods/access fields that are final or have environment issues |
| `src/main/java/com/kingodogo/buildscape/mixin/FeatureMixin.java` | 19-22 | block | * * Prevents tree features from replacing Composter planters with dirt * when a tree grows on top of or near a planter block. |
| `src/main/java/com/kingodogo/buildscape/mixin/FireworkStarterMixin.java` | 51 | line | Extract rotation/facing yaw (from shooter NBT or trajectory or camera) |
| `src/main/java/com/kingodogo/buildscape/mixin/FireworkStarterMixin.java` | 61 | line | Expand bounding box for custom shapes so long-distance viewing and wide frustums don't cull particles |
| `src/main/java/com/kingodogo/buildscape/mixin/FlowingFluidMixin.java` | 40 | line | Query the supply only during fluid simulation; pipes remain empty to the global fluid/render APIs. |
| `src/main/java/com/kingodogo/buildscape/mixin/FlowingFluidMixin.java` | 70 | line | 1. Spreading OUT of a Hollow Pipe: strictly only allowed through open hollow endpoints |
| `src/main/java/com/kingodogo/buildscape/mixin/FlowingFluidMixin.java` | 77 | line | 2. Spreading OUT of a Hollow Log: strictly only allowed through open hollow ends |
| `src/main/java/com/kingodogo/buildscape/mixin/FlowingFluidMixin.java` | 85 | line | 3. Spreading INTO a Hollow Pipe: strictly only allowed into open hollow endpoints |
| `src/main/java/com/kingodogo/buildscape/mixin/FlowingFluidMixin.java` | 92 | line | 4. Spreading INTO a Hollow Log: strictly only allowed into open hollow ends |
| `src/main/java/com/kingodogo/buildscape/mixin/ItemMixin.java` | 18 | line | Only water bottles should be stackable to 16 |
| `src/main/java/com/kingodogo/buildscape/mixin/LeavesBlockMixin.java` | 31 | line | --- Waterlogging --- |
| `src/main/java/com/kingodogo/buildscape/mixin/LeavesBlockMixin.java` | 64 | line | --- Fast Leaf Decay --- |
| `src/main/java/com/kingodogo/buildscape/mixin/LiquidBlockRendererMixin.java` | 50 | line | Hollow log and steel-pipe water is rendered as internal channel state, not as a |
| `src/main/java/com/kingodogo/buildscape/mixin/LiquidBlockRendererMixin.java` | 51 | line | vanilla full-block fluid. Treat the block shell as a boundary so vanilla fluid |
| `src/main/java/com/kingodogo/buildscape/mixin/LiquidBlockRendererMixin.java` | 52 | line | does not clip through the side walls or create full-width bulges. |
| `src/main/java/com/kingodogo/buildscape/mixin/PauseScreenMixin.java` | 32 | line | Default fallback positions |
| `src/main/java/com/kingodogo/buildscape/mixin/PauseScreenMixin.java` | 39 | line | Dynamically find the Statistics button to align perfectly next to it |
| `src/main/java/com/kingodogo/buildscape/mixin/PauseScreenMixin.java` | 58 | line | Align perfectly next to the Statistics button when it is there. |
| `src/main/java/com/kingodogo/buildscape/mixin/RenderBuffersMixin.java` | 24 | line | BufferSource retains this map; constructor names remain valid in production mappings. |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 57 | line | Recursion guard: prevent infinite loops if other mods hook into item rendering |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 62 | line | Check if player has Shulker Preview enabled in Player Rules |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 67 | line | Check if Shift key is pressed and a valid item is being hovered |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 90 | line | 1. Calculate vanilla text tooltip bounding box |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 121 | line | 2. Position custom tooltip directly below the vanilla text tooltip box |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 122 | line | Vanilla tooltip box bounds: (textX - 3, textY - 3) to (textX + textTooltipWidth + 3, textY + textTooltipHeight + 3) |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 124 | line | 3px border bottom + 3px gap |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 126 | line | 3. Screen bounds check for custom box: |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 127 | line | If it doesn't fit below the vanilla text tooltip, place it right above the text tooltip! |
| `src/main/java/com/kingodogo/buildscape/mixin/ScreenMixin.java` | 142 | line | 4. Render custom tooltip image (9-slice frame & items) |
| `src/main/java/com/kingodogo/buildscape/mixin/StonecutterScreenMixin.java` | 29 | line | Output slot is centered around x=143 and ends at x=159 (16px slot contents, 18px border/outline from x=142 to x=160). |
| `src/main/java/com/kingodogo/buildscape/mixin/StonecutterScreenMixin.java` | 30 | line | Making our button 18px wide aligns it perfectly with the output slot. |
| `src/main/java/com/kingodogo/buildscape/mixin/StonecutterScreenMixin.java` | 32 | line | Moved higher up to create separation from the output slot |
| `src/main/java/com/kingodogo/buildscape/mixin/StonecutterScreenMixin.java` | 45 | line | Dark border |
| `src/main/java/com/kingodogo/buildscape/mixin/StonecutterScreenMixin.java` | 47 | line | Green when active, dark grey when inactive |
| `src/main/java/com/kingodogo/buildscape/mixin/StonecutterScreenMixin.java` | 53 | line | Slider handle (6px wide inside an 18px wide button) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersPouchMenu.java` | 24 | line | Item positions read off builders_pouch_ui.png; the slot frames are baked into |
| `src/main/java/com/kingodogo/buildscape/network/BuildersPouchMenu.java` | 25 | line | that sheet, so these must match it exactly or the items sit off-centre. |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 15 | line | ── LAYOUT ──────────────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 16 | line | Slot coordinates are the *item* positions (the slot frame is drawn at -1/-1). |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 17 | line | They are taken straight from the background artwork and MUST stay in sync |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 18 | line | with the layout constants in BuildersWorkbenchScreen. |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 19 | line | Colour Builder (184 x 203) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 24 | line | Gradient Builder (206 x 203) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 29 | line | Player inventory - identical on both tabs |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 34 | line | ── Client-side constructor ──────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 40 | line | ── Server-side constructor ─────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 48 | line | ── Tab 0: Color Picker Layout Slots (Indices 0 to 47) ───────────────── |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 50 | line | Slot 0: Pipette/tool slot (index 0, active on Tab 0) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 63 | line | Slots 1–9: Color Presets grid (indices 1–9, active on Tab 0) - Read-Only |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 85 | line | Slot 10: Input Pouch (index 10, active on Tab 0) - Pouch Only |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 98 | line | Slot 11: Output Pouch (index 11, active on Tab 0) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 111 | line | Player Inventory slots for Tab 0 (Indices 12 to 47) - aligned with H=192 / W=176 layout |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 131 | line | ── Tab 1: Gradient Builder Layout Slots (Indices 48 to 103) ─────────── |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 133 | line | Slots 12–20: Gradient Output (indices 12–20, active on Tab 1) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 143 | line | Slot 10: Input Pouch (index 10, active on Tab 1) - Pouch Only |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 156 | line | Slot 11: Output Pouch (index 11, active on Tab 1) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 169 | line | Slots 21–29: Gradient Inputs row (indices 21–29, active on Tab 1) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 179 | line | Player Inventory slots for Tab 1 (Indices 68 to 103) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 200 | line | This method is now a no-op as all coordinates are statically set in the constructor |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 238 | line | ── Quick-move (shift-click) ────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 266 | line | On Tab 0, route block items only to Slot 0 (Pipette input) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 269 | line | On Tab 1, route block items to Gradient Inputs (indices 59 to 67) |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchMenu.java` | 296 | line | ── Inner slot types ────────────────────────────────────────────────────── |
| `src/main/java/com/kingodogo/buildscape/network/BuildersWorkbenchResultsPacket.java` | 19-22 | block | * * Applies nine client-computed ghost results. No items are created: the server * validates every registry id and the workbench result slots remain read-only. |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 58 | line | Must be holding a Hammer in main hand |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 62 | line | Must have a block in offhand |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 69 | line | Never replace unmineable blocks (bedrock, barriers, command blocks, etc.) |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 73 | line | Iron hammer cannot replace obsidian-level blocks (destroyTime >= 50.0f) |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 77 | line | Don't replace the same block |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 81 | line | Get drops from the old block before removing it |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 87 | line | Silk touch: give back the block as if silk-touched |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 103 | line | Place the replacement block |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 108 | line | Consume one block from offhand |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 113 | line | Drop the old block items |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 118 | line | Damage the hammer |
| `src/main/java/com/kingodogo/buildscape/network/HammerReplacePacket.java` | 123 | line | Effects |
| `src/main/java/com/kingodogo/buildscape/network/RemovePillarPacket.java` | 56 | line | IMPORTANT: Save immediately to file so it's persistent |
| `src/main/java/com/kingodogo/buildscape/network/RemovePillarPacket.java` | 59 | line | Broadcast the updated list to all players so their GUIs sync |
| `src/main/java/com/kingodogo/buildscape/network/RemovePillarPacket.java` | 65 | line | Force sync pillars in the world so they stop using the removed IDs |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 11-14 | block | * * Sent from client to server to request a fresh sync of pillar IDs. * This ensures the GUI always has the latest data when opened. |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 21 | line | No data needed |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 29 | line | No data needed |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 39 | line | Get pillar data from server and send it to the requesting client |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 42 | line | Ensure manager is loaded |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 47 | line | Sync colors from NBT before sending to ensure freshest data |
| `src/main/java/com/kingodogo/buildscape/network/RequestPillarIdsPacket.java` | 53 | line | Get all pillar data and send to client |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 66 | line | Player must be crouching and holding Wrench |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 107 | line | 1. Handle Slab Type / Half (UP / DOWN toggles top/bottom) |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 126 | line | 2. Handle 6-Way Facing |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 144 | line | 3. Handle Horizontal Facing |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 157 | line | 4. Handle Axis (Pillars / Logs) |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 180 | line | 5. Handle 16-Rotation (Signs / Banners / Skulls) |
| `src/main/java/com/kingodogo/buildscape/network/RotateBlockPacket.java` | 190 | line | 6. Generic Fallback Block Rotation |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 15-17 | block | * * Syncs pillar IDs data from server to client so the GUI can display it. |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 32 | line | Max string length |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 58 | line | IMPORTANT: Clear existing data and prepare for server sync |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 59 | line | This resets the isServerSynced flag so new data can be loaded |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 62 | line | Add all pillar data from server |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 67 | line | Mark as loaded so GUI can display |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 68 | line | This also sets isServerSynced=true to indicate we have fresh server data |
| `src/main/java/com/kingodogo/buildscape/network/SyncPillarIdsPacket.java` | 71 | line | Refresh GUI if it's open |
| `src/main/java/com/kingodogo/buildscape/network/SyncSignFramePacket.java` | 14-16 | block | * * Synchronizes cosmetic frame attachments on signs from server to clients. |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 20 | line | Limit to prevent server lag |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 93 | line | BFS to find all blocks |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 97 | line | Neighbors |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 118 | line | Start the sequential breaking with a delay |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 133 | line | Run this batch |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 140 | line | Only spawn particles for every 3rd block to reduce spam |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 147 | line | Schedule next batch with a proper delay (2 ticks = 100ms) |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 148 | line | Incremental speed: Increase batch size every 3 ticks |
| `src/main/java/com/kingodogo/buildscape/network/TreeChopPacket.java` | 159 | line | Schedule next batch with a 100ms (~2 ticks) delay without blocking the server thread |
| `src/main/java/com/kingodogo/buildscape/network/UpdateAllPillarIdsPacket.java` | 67 | line | Optionally broadcast to all players so their GUIs stay in sync |
| `src/main/java/com/kingodogo/buildscape/network/UpdateAllPillarIdsPacket.java` | 73 | line | Force sync pillars in the world |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 38 | line | Only OP can change global config (level 2) |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 44 | line | Capture old pattern for transition logic |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 66 | line | Notify all players about the new config |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 72 | line | Transition logic: Identify which pillars follow the new global config and which stick to their current state. |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 73 | line | As requested, customized pillars (dyed OR pattern-overridden) should NOT be affected by the global pattern button. |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 80 | line | Robust check for modification: has colors or has hardcoded pattern settings |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 85 | line | If it's customized in any way but currently following the global pattern (no hard override yet), |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 86 | line | we MUST lock its effective pattern to the OLD one before the global change takes over. |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 88 | line | Lock to the pattern it was using BEFORE this global change |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 92 | line | Unmodified pillar: Clear its overrides to ensure it follows the new global config precisely. |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 107 | line | Sync ALL relevant pillars to ensure they pick up their updated settings (either new global or newly locked override) |
| `src/main/java/com/kingodogo/buildscape/network/UpdateConfigPacket.java` | 122 | line | Force block update to ensure client recognizes the pattern lock immediately |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 50 | line | Read nullable pattern |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 53 | line | Read nullable boolean |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 56 | line | Read nullable doubles |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 61 | line | Read nullable integer |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 64 | line | Read color list |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 79 | line | Write nullable pattern |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 85 | line | Write nullable boolean |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 91 | line | Write nullable doubles |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 107 | line | Write nullable integer |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 113 | line | Write color list |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 132 | line | Update the pillar data in the manager |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 174 | line | Update the block entity NBT |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 186 | line | Find the level for this pillar's dimension |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 206 | line | Find the bottom of the stack |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 214 | line | Update NBT with settings from manager |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 217 | line | Update pattern |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 226 | line | Update pattern speed |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 235 | line | Update pattern spread |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 244 | line | Update pattern intensity |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 253 | line | Update use_pattern toggle |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 262 | line | Update max particle color |
| `src/main/java/com/kingodogo/buildscape/network/UpdatePillarDataPacket.java` | 271 | line | Update colors |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 19 | line | Use custom bubble sprite |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 23 | line | Bubble properties - match snowflake trail properties |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 24 | line | Gentle fall (same as snowflake) |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 25 | line | 60-100 ticks lifetime (same as snowflake) |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 28 | line | Random size variation (same as snowflake) |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 29 | line | 0.1 to 0.2 size |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 31 | line | Slight drift (same as snowflake) |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 36 | line | Bubbles are white/transparent |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 38 | line | 0.8 to 1.0 (same as snowflake) |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 45 | line | Wobble effect |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 49 | line | Fade out before popping |
| `src/main/java/com/kingodogo/buildscape/particle/BubbleParticle.java` | 63 | line | Full brightness |
| `src/main/java/com/kingodogo/buildscape/particle/CakeParticle.java` | 18 | line | Static map to store color queues for particles |
| `src/main/java/com/kingodogo/buildscape/particle/CakeParticle.java` | 19 | line | Key: "x,y,z" string, Value: ColorEntry |
| `src/main/java/com/kingodogo/buildscape/particle/CakeParticle.java` | 45 | line | Pick random texture from the set |
| `src/main/java/com/kingodogo/buildscape/particle/CakeParticle.java` | 48 | line | Color handling logic |
| `src/main/java/com/kingodogo/buildscape/particle/CakeParticle.java` | 61 | line | Default white |
| `src/main/java/com/kingodogo/buildscape/particle/CakeParticle.java` | 102 | line | Removed setSpriteFromAge to keep the selected static texture |
| `src/main/java/com/kingodogo/buildscape/particle/CascadeParticle.java` | 51 | line | Float up gently underwater |
| `src/main/java/com/kingodogo/buildscape/particle/CascadeParticle.java` | 53 | line | Fall down in air |
| `src/main/java/com/kingodogo/buildscape/particle/CascadeParticle.java` | 56 | line | Move directly and update bounding box so particles render through water and frustum cull correctly |
| `src/main/java/com/kingodogo/buildscape/particle/CascadeParticle.java` | 62 | line | Fade out in the last 40% of lifetime |
| `src/main/java/com/kingodogo/buildscape/particle/CascadeParticle.java` | 72 | line | Full brightness so particles are visible underwater |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 15 | line | Static map to store color queues for particles |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 16 | line | Key: "x,y,z" string, Value: ColorEntry |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 45 | line | Logic to select specific texture from the 12 files |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 46 | line | 0-5: Shape 1 (6 color themes) |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 47 | line | 6-11: Shape 2 (6 color themes) |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 49 | line | "Change color theme on the go": Cycle through 6 themes based on time |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 51 | line | Randomly select a theme instead of time-based cycling |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 54 | line | "Spawn both shapes": Randomly pick Shape 1 or Shape 2 for this theme |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 58 | line | Select the specific static sprite |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 59 | line | sprites.get(i, total) maps i to the sprite at that fraction of the list |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 62 | line | Scale down the particle |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 65 | line | Color handling logic |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 78 | line | Default white |
| `src/main/java/com/kingodogo/buildscape/particle/CherryParticle.java` | 121 | line | Removed setSpriteFromAge to keep the selected static texture |
| `src/main/java/com/kingodogo/buildscape/particle/ColoredSmokeParticle.java` | 25 | line | Pick one random sprite and keep it (no animation) |
| `src/main/java/com/kingodogo/buildscape/particle/ColoredSmokeParticle.java` | 28 | line | Get color from position queue |
| `src/main/java/com/kingodogo/buildscape/particle/ColoredSmokeParticle.java` | 71 | line | Fade out |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 17 | line | Red |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 18 | line | Cyan |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 19 | line | Blue |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 20 | line | Light Blue |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 21 | line | Yellow |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 22 | line | Orange |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 23 | line | Lime |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 24 | line | Green |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 25 | line | Pink |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 26 | line | Purple |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 27 | line | Magenta |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 33 | line | Pick a random sprite from the 7 confetti textures (confetti_1 through confetti_7) |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 38 | line | Pick a random vibrant color from the 11 base pillar colors |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 45 | line | Confetti physics - more realistic falling |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 46 | line | Variable light gravity for gentle float |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 47 | line | 70-110 ticks lifetime |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 50 | line | Random size variation |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 51 | line | 0.08 to 0.2 size |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 53 | line | Random initial rotation |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 57 | line | Random rotation speed for tumbling effect |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 60 | line | Add air resistance effect - particles slow down over time |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 61 | line | Initial velocity is set, but we'll modify it in tick() |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 66 | line | Full opacity |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 74 | line | Update rotation with acceleration for tumbling effect |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 77 | line | Apply rotation acceleration for more realistic tumbling |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 78 | line | (simplified - in real physics this would be more complex) |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 80 | line | Air resistance - particles slow down horizontally over time |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 84 | line | Add slight horizontal drift for more realistic confetti movement |
| `src/main/java/com/kingodogo/buildscape/particle/ConfettiParticle.java` | 90 | line | Fade out near the end |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 54 | line | Custom lifetime: 200-300 game ticks (10-15 seconds) |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 59 | line | Slow drift velocities |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 64 | line | Small particle size - increased to 0.08F-0.13F as requested |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 67 | line | Start invisible, fade in then out |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 71 | line | Opaque pale yellow glow color, like requested |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 96 | line | Gentle random drift |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 101 | line | Clamp velocities |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 108 | line | Fade in/out: smooth pulse |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 111 | line | Fade in over first 30% |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 114 | line | Fade out over last 30% |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 117 | line | Remain fully opaque |
| `src/main/java/com/kingodogo/buildscape/particle/FireflyParticle.java` | 134-136 | block | * * Makes the particle glow (full brightness regardless of world light) |
| `src/main/java/com/kingodogo/buildscape/particle/GeyserPlumeParticle.java` | 29 | line | In 1.18.2, speedUpWhenYMotionIsBlocked isn't a field on Particle, so we can omit it or set whatever is needed |
| `src/main/java/com/kingodogo/buildscape/particle/NoxiousGasParticle.java` | 25 | line | Rise up slowly |
| `src/main/java/com/kingodogo/buildscape/particle/NoxiousGasParticle.java` | 31 | line | Custom texture has color |
| `src/main/java/com/kingodogo/buildscape/particle/NoxiousGasParticle.java` | 50 | line | Rise up and drift |
| `src/main/java/com/kingodogo/buildscape/particle/NoxiousGasParticle.java` | 58 | line | Slow fade out |
| `src/main/java/com/kingodogo/buildscape/particle/PillarSparkleParticle.java` | 141 | line | Crop 2% from the left to remove potential halos |
| `src/main/java/com/kingodogo/buildscape/particle/PillarSparkleParticle.java` | 149 | line | Crop 2% from the right to remove potential halos |
| `src/main/java/com/kingodogo/buildscape/particle/PillarSparkleParticle.java` | 159 | line | Crop 2% of the frame height from the top |
| `src/main/java/com/kingodogo/buildscape/particle/PillarSparkleParticle.java` | 169 | line | Crop 2% of the frame height from the bottom |
| `src/main/java/com/kingodogo/buildscape/particle/SmokeColorRegistry.java` | 7-10 | block | * * Stores pending smoke colors keyed by spawn position. * Not client-only so SmokeVentBlock can reference it without dist-cleaner issues. |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeParticle.java` | 23 | line | 10-20 seconds |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeStillParticle.java` | 18 | line | No gravity as requested |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeStillParticle.java` | 19 | line | Match pillar snowflake lifetime |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeStillParticle.java` | 20 | line | Match pillar snowflake physics |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeStillParticle.java` | 22 | line | Match pillar snowflake size |
| `src/main/java/com/kingodogo/buildscape/particle/SnowflakeStillParticle.java` | 29 | line | Match pillar snowflake alpha |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 21 | line | Static map to store color queues for particles |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 22 | line | Key: "x,y,z" string, Value: ColorEntry |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 46 | line | Use sprite set |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 49 | line | Heart particle movement logic (similar to vanilla heart) |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 53 | line | Longer life |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 55 | line | Initial upward velocity if none provided |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 60 | line | Color handling logic (copied from PillarSparkleParticle) |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 74 | line | Default Red for heart |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 78 | line | Default size |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 79 | line | No collision |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 102 | line | Red fallback |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 129 | line | Simple float up movement |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 142 | line | Fade out |
| `src/main/java/com/kingodogo/buildscape/particle/TintableHeartParticle.java` | 156 | line | Full brightness |
| `src/main/java/com/kingodogo/buildscape/particle/TrailNoteParticle.java` | 18 | line | Static map to store color queues for particles |
| `src/main/java/com/kingodogo/buildscape/particle/TrailNoteParticle.java` | 44 | line | Pick random texture from the set |
| `src/main/java/com/kingodogo/buildscape/particle/TrailNoteParticle.java` | 47 | line | Color handling logic |
| `src/main/java/com/kingodogo/buildscape/particle/TrailNoteParticle.java` | 60 | line | Random color |
| `src/main/java/com/kingodogo/buildscape/particle/TrailNoteParticle.java` | 62 | line | Use HSB for vibrant colors |
| `src/main/java/com/kingodogo/buildscape/particle/XpParticle.java` | 59 | line | Full brightness |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 19 | line | Bubble column logic and entity physics inside Hollow Steel Pipes |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 49 | line | 1. Calculate aggregate horizontal & vertical flow vector |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 66 | line | 2. Vertical bubble column physics (highest priority) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 82 | line | 3. Fluid stream pushing physics for items, mobs, and players |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 91 | line | Downward waterfall / downward pipe channel |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 94 | line | Items float with water buoyancy inside horizontal pipes |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 102 | line | Spawns flow particles and bubble column effects inside pipe cavity |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 108 | line | 1. If bubble column is active, spawn dense vertical elevator particles |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 113 | line | 2. Spawn directional flow bubbles indicating the physical movement of water |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 116 | line | Standing contained water without flow: occasional gentle ambient bubble |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 142 | line | For each active flow direction: |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 143 | line | - Spawn 1-2 bubbles that travel along the flow direction (from entry face to exit face). |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 144 | line | - If this is an open endpoint in this direction, also spray bubbles slightly outside. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 148 | line | 1–2 particles per direction per frame |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 153 | line | Open endpoint spray: shoot extra particles outside the pipe exit face |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 154 | line | so water visually "comes out" of the pipe end. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 156 | line | Check if this direction is actually the exit (not connected to another pipe) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 157 | line | We spawn 1-3 extra particles slightly outside the face |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 164 | line | Occasional subtle flowing water sound effect |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 176-179 | block | * * Spawns a single bubble particle that floats directly on the WATER SURFACE * and travels from the UPSTREAM entry face of this pipe toward the DOWNSTREAM exit face. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 188 | line | Floats directly on the water surface! |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 192 | line | Flow: +X |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 201 | line | Flow: -X |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 210 | line | Flow: +Z |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 219 | line | Flow: -Z |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 228 | line | Flow: -Y (Vertical Drop) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 237 | line | Flow: +Y (Bubble Elevator) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 254-257 | block | * * Spawns a small spray of bubble and splash particles OUTSIDE the pipe exit face. * This gives the visual appearance of water leaving the pipe at the open endpoint. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 263 | line | Move to the exit face center |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnHandler.java` | 285-287 | block | * * Spawns contained bubble column particles and ambient sounds within the 1x1 pipe cavity. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/BubbleColumnState.java` | 7-12 | block | * * Represents the bubble column state inside a hollow pipe. * - NONE: Normal water flow or no bubble column. * - UP: Upward bubble column powered by Soul Sand. Allows upward fluid movement and lifts entities. * - DOWN: Downward bubble colu... |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/HollowPipeTransportManager.java` | 19 | line | Coordinator for Hollow Steel Pipe fluid transport network updates |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/HollowPipeTransportManager.java` | 35-37 | block | * * Immediately processes and clears pending recalculations for the specified level. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/HollowPipeTransportManager.java` | 65 | line | Process pending BFS pipe recalculations |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFlowState.java` | 15-18 | block | * * Encapsulates the active fluid transport state of an individual Hollow Steel Pipe. * Completely decoupled from the structural blockstate (such as WATERLOGGED). |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFlowState.java` | 27 | block | * Total distance from source to the furthest pipe in this network section. Used for slope computation. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFlowState.java` | 29 | block | * True when this pipe has an open endpoint (flow exits the network here). Used for slope rendering. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFlowState.java` | 52 | block | * Convenience constructor (legacy - maxDistance=0, isOpenEndpoint=false) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 15-18 | block | * * Abstract extensible base class for fluid transport through Hollow Steel Pipes. * Treats the existing pipe topology as the absolute authority. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 21-23 | block | * * The fluid type managed by this transport instance (e.g. Fluids.WATER). |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 26-29 | block | * * Recalculates the connected pipe network starting from the given position. * Returns the set of BlockPos in the discovered connected pipe component. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 32-34 | block | * * Checks if a blockstate is a Hollow Pipe. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 39-41 | block | * * Checks if two Hollow Pipe blockstates are connected in direction dir based on topology properties. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 55-57 | block | * * Checks if there is a valid connected topology passage from fromPos in direction dir. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 65-67 | block | * * Returns a list of all directions in which this pipe blockstate has an active connected branch. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeFluidTransport.java` | 81-83 | block | * * Checks if direction dir is an open endpoint on the given pipe blockstate. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeItemTransit.java` | 11-14 | block | * * Encapsulates an item stack in virtual transit through a Hollow Steel Pipe network. * Completely eliminates server ItemEntity physics overhead and chunk collision lag. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeOutletWater.java` | 7 | block | * Water supplied to vanilla through a directed pipe outlet. Author: HoYin1600p. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 8-11 | block | * * Topology abstraction interface allowing the fluid transport simulation to query * connection passages and endpoints without direct coupling to Minecraft internal registry lifecycles. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 14-16 | block | * * Checks if a hollow pipe block exists at the given position. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 19-21 | block | * * Checks if an internal 1x1 passage is connected from pos in the given direction. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 24-26 | block | * * Checks if the given direction is an open endpoint at pos. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 29-31 | block | * * Detects if an active bubble column base (Soul Sand or Magma Block) is present below pos. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 34-36 | block | * * Checks if pos is a water source (either waterlogged or intaking from external world water). |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 39-46 | block | * * Returns the horizontal vanilla-flow distance at which water enters this * pipe. A bucket-waterlogged pipe is the source itself (0); a pipe fed by * an adjacent world-water source is the first flowing block (1). * * The default preserves... |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/PipeTopologyAccess.java` | 51-54 | block | * * Face through which an external world-water source enters this pipe. * Bucket-filled pipes have no external inlet, so the default is null. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 19-23 | block | * * Concrete implementation of PipeFluidTransport for Water. * Implements deterministic flow propagation, priority rules (DOWN > Straight > Branch > UP with Bubble Column), * bubble-column elevators via Soul Sand / Magma, per-branch exponen... |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 76 | line | 1. Discover the connected pipe component |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 82 | line | 2. Identify all water sources |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 90 | line | 3. If NO sources exist, clear transport state across the entire component |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 97 | line | Animate drainage: delay by distance * 2 ticks so water recedes naturally |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 105 | line | Clear WATER_LEVEL so the pipe stops acting as a fluid flowing state. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 106 | line | Use flag 2 (UPDATE_CLIENTS) to avoid triggering neighborChanged/BFS loop. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 115 | line | 4. Simulate deterministic flow propagation from sources |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 118 | line | 5. Apply new states to all block entities, update WATER_LEVEL blockstate, and handle outflows |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 125 | line | 5a. Update block entity (PipeFlowState for renderer / bubble columns / etc.) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 131 | line | 5b. Sync WATER_LEVEL blockstate property (1..7 based on distance). |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 132 | line | Flag 2 = UPDATE_CLIENTS only: sends to clients without calling neighborChanged |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 133 | line | on adjacent blocks, which would re-trigger the BFS in an infinite loop. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 147 | line | flag 2 = notify clients only |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 150 | line | 5c. Outflow: immediately place flowing water at open endpoints and schedule |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 151 | line | a continuous block tick so the pipe keeps refreshing those blocks. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 164-166 | block | * * Core simulation algorithm operating through the PipeTopologyAccess interface. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 186 | line | An external source occupies an inlet face. Recording it here |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 187 | line | prevents that same face being reclassified as an outflow. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 190 | line | If source has bubble column base directly below, activate it immediately |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 217 | line | Check if this pipe has a bubble column base directly below |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 223 | line | Determine prioritized exit directions to connected pipes |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 235 | line | A downward vertical drop or upward bubble column resets the horizontal distance counter |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 238 | line | Restrict horizontal flow to vanilla-accurate 7 blocks max |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 252 | line | Propagate bubble column state upward or downward through contiguous vertical column |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 272 | line | Also check for open endpoints (physical openings into the world) on this pipe |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 273 | line | E.g., open straight continuation, open bottom, open sides |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 276 | line | Don't flow backward into the entry face |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 290 | line | --- Post-processing: Compute per-branch maxDistance and mark open endpoints --- |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 291 | line | Each branch calculates its own maxDistance along its downstream path from the source. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 292 | line | This ensures every branch independently calculates its own exponential slope and reaches |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 293 | line | full drop at its own end. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 301 | line | If this pipe has an open endpoint direction, mark it |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 317-319 | block | * * Recursively traverses downstream flow paths to find the maximum distance reachable in this branch. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 344-350 | block | * * Deterministic exit direction selector following strict priorities: * 1. DOWN (Gravity first) * 2. Straight line continuation (momentum along current direction) * 3. Horizontal branches (deterministic order: NORTH > SOUTH > WEST > EAST) ... |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 359 | line | Priority 1: DOWN (gravity) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 364 | line | Priority 2: Straight line continuation (forward momentum) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 374 | line | Priority 3: Horizontal side branches (deterministic order) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 376 | line | Don't flow backward into entrance |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 377 | line | Already added straight continuation |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 383 | line | Priority 4: UP (only valid with upward bubble column) |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 397 | line | Pass the directed exits as well as the BFS distance. A pipe may have |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 398 | line | more than one physical opening, but only its downstream endpoint is |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 399 | line | allowed to create a vanilla flowing-water block. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 405 | line | Intentionally empty: when a pipe loses water, we simply stop refreshing |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 406 | line | the outflow blocks. Vanilla fluid physics will naturally evaporate any |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 407 | line | flowing water at the endpoints within a few ticks — no manual removal needed. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 408 | line | Manually removing water here caused legitimate world water (rivers, other |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WaterPipeTransport.java` | 409 | line | pipe networks) to be incorrectly destroyed. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WorldPipeTopologyAccess.java` | 11-14 | block | * * Real-world adapter implementing PipeTopologyAccess by querying Minecraft's Level / BlockGetter * and existing Hollow Steel Pipe blockstates. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WorldPipeTopologyAccess.java` | 48-58 | block | * * Checks if this pipe position is an AUTHORITATIVE WATER SOURCE. * * A pipe is a source if and only if: * (a) Its blockstate has WATERLOGGED=true (water bucket was placed directly into this pipe), OR * (b) It has an open endpoint that dir... |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WorldPipeTopologyAccess.java` | 64 | line | (a) Waterlogged blockstate: a water bucket was explicitly placed into this pipe. |
| `src/main/java/com/kingodogo/buildscape/pipe/transport/WorldPipeTopologyAccess.java` | 79 | line | The intake pipe is itself vanilla's first flowing-water block. |
| `src/main/java/com/kingodogo/buildscape/recipe/CustomFireworkStarRecipe.java` | 51 | line | Dyes CANNOT be used when crafting fixed-palette shapes (Cake, Crown, Trophy, Christmas Tree, Snowflake) |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java` | 30-33 | block | * * Primary Datapack Reload Listener for the BuildScape Dynamic Compact Recipe Engine (BDRE). * Hooks into AddReloadListenerEvent to stream, compile, cache, and inject 10,000+ custom recipes. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java` | 89 | line | Step 1: Collect resources and compute hash |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java` | 109 | line | Step 2A: Check Bundled JAR Binary Cache |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java` | 127 | line | Step 2B: Check Local Disk Binary Cache |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java` | 140 | line | Step 3: Stream and Compile (Parallelized across CPU cores) |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/BuildScapeRecipeLoader.java` | 159 | line | Step 4: Save Binary Cache |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 39-45 | block | * * High-performance binary recipe cache (.bscb). * Serializes and deserializes compiled recipe graphs into compact binary * streams with string dictionary pool interning. * Supports loading bundled binary cache directly from JAR resources ... |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 136 | line | Write Recipes and build string pool dynamically |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 227 | line | Read String Pool |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 234 | line | High-Performance Caching Pools for O(1) constant-time interning |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 239 | line | Multi-threaded Parallel Pre-Warm of String Pool Interning |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 264 | line | Read Recipes |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 317 | line | Type Shaped Durability |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 326 | line | Type Shapeless Durability |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 333 | line | Type Shaped |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 342 | line | Type Shapeless |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 349 | line | Type Stonecutter |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 352 | line | Smelting |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 357 | line | Blasting |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 362 | line | Smoking |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 367 | line | Campfire |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 372 | line | Smithing |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 376 | line | Confetti Configure |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 378 | line | Clear Shulker Filters |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 427 | line | Shaped |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 437 | line | Shapeless |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 445 | line | Stonecutter |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 449 | line | Smelting |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 455 | line | Blasting |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 461 | line | Smoking |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 467 | line | Campfire |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 473 | line | Smithing (UpgradeRecipe) |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 478 | line | ShapedDurability |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 489 | line | ShapelessDurability |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 497 | line | ConfettiConfigure |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/cache/BinaryRecipeCache.java` | 500 | line | ClearShulkerFilters |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/AliasResolver.java` | 14-16 | block | * * Resolves short aliases, namespace prefixes, item IDs, and tag references. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/AliasResolver.java` | 22 | line | Default built-in namespace aliases |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/AliasResolver.java` | 44 | line | Direct alias match |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/AliasResolver.java` | 49 | line | Replace prefixes |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/AliasResolver.java` | 58 | line | Default namespace fallback if missing namespace |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java` | 17-19 | block | * * Main compilation engine that converts intermediate category IR into native Minecraft Recipe objects. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java` | 40 | line | Phase 1: Register Aliases |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java` | 43 | line | Phase 2: Register Templates |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java` | 46 | line | Phase 3: Family Expansion |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/BuildScapeRecipeCompiler.java` | 65 | line | Phase 4: Direct Recipes |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/FamilyExpander.java` | 9-12 | block | * * Expands family specifications (wood family, stone family, brick family) into complete sets of crafting/stonecutting recipes. * Supports auto-detection, explicit wood/stone sections, variant inclusion/exclusion lists, and reversible conv... |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/FamilyExpander.java` | 34 | line | Intelligent auto-detection for prefix and family type if unspecified |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/FamilyExpander.java` | 37 | line | e.g. BS:ashpen_gray_ |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/FamilyExpander.java` | 45 | line | Default targets per family type |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/FamilyExpander.java` | 90 | line | Add stonecutter shortcut for building block variants if stone family |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/compiler/TemplateEngine.java` | 7-9 | block | * * Reusable recipe template engine. Expands parametric recipe shapes using placeholder replacement. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/integration/RecipeManagerInjector.java` | 14-17 | block | * * Injects compiled BDRE recipes into Minecraft's RecipeManager internal map structures. * Guarantees compatibility with Crafting Tables, Furnaces, Stonecutters, Network Sync, and JEI. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/integration/RecipeManagerInjector.java` | 63 | line | Create mutable maps if unmodifiable |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/integration/RecipeManagerInjector.java` | 83 | line | Set back internal maps |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/parser/RecipeIR.java` | 6-9 | block | * * Intermediate Representation (IR) structures for BDRE recipes. * These records represent parsed template rules, family generators, and inline recipe definitions. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/parser/StreamingRecipeParser.java` | 11-14 | block | * * High-performance streaming JSON reader using GSON JsonReader. * Supports wood/stone family categories, reversal/reciprocal auto-recipes, and compact array recipe formats. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/parser/StreamingRecipeParser.java` | 291 | line | Add Primary Forward Recipe (Result = resultItem, Input = input) |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/parser/StreamingRecipeParser.java` | 295 | line | Add Reversal Recipe if reversible is true and input is a single item/tag |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/util/IngredientCache.java` | 11-14 | block | * * Caches and canonicalizes Ingredient instances across 10,000+ recipes. * Reusing Ingredient instances saves significant heap memory and eliminates duplicate array allocations. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/validation/RecipeValidator.java` | 12-15 | block | * * Validates parsed recipe specifications before building Minecraft Recipe objects. * Prevents registration of recipes with unregistered items, invalid shapes, or duplicate IDs. |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/validation/RecipeValidator.java` | 25 | line | Validate Result Item |
| `src/main/java/com/kingodogo/buildscape/recipe/framework/validation/RecipeValidator.java` | 38 | line | Validate Ingredients |
| `src/main/java/com/kingodogo/buildscape/recipe/ShapedDurabilityRecipe.java` | 132 | line | Find first and last non-empty rows |
| `src/main/java/com/kingodogo/buildscape/recipe/ShapedDurabilityRecipe.java` | 144 | line | Find bounds for columns |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 251 | line | break |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 252 | line | step |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 253 | line | place |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 254 | line | hit |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 255 | line | fall |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 266 | line | break |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 267 | line | step |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 268 | line | place |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 269 | line | hit |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 270 | line | fall |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 281 | line | break |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 282 | line | step |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 283 | line | place |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 284 | line | hit |
| `src/main/java/com/kingodogo/buildscape/sound/ModSounds.java` | 285 | line | fall |
| `src/main/java/com/kingodogo/buildscape/stat/ModStats.java` | 30 | line | Header category marker stats |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 18 | line | Single clean rectangular hitboxes per trophy type |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 19 | line | Tall pillar / jar / froglight / hammer trophies (exceeding 1-block height by ~2 pixels) |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 31 | line | Wide Christmas stand trophies (Ornament, Stocking, Star, String Light) |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 36 | line | Buildscape Statuette Trophies (Gold, Emerald, Diamond, Netherite) |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 41 | line | 1. Pillar Trophies (Stone, Gold, Diamond, Netherite, Emerald) |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 96 | line | 2. Thematic / Item Trophies |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 166 | line | 3. Hammer & Buildscape Milestone Trophies |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 229 | line | Register Block |
| `src/main/java/com/kingodogo/buildscape/trophy/Trophies.java` | 236 | line | Register Item |
| `src/main/java/com/kingodogo/buildscape/util/ColorGradientSolver.java` | 76-80 | block | * * Nine-slot color and gradient solver. The logical server uses MapColor as a * safe fallback; clients replace that catalog with colors sampled from baked * models when the Builder's Workbench is opened. |
| `src/main/java/com/kingodogo/buildscape/util/ColorGradientSolver.java` | 86 | line | Low bits enable categories; the matching high bits apply shift-click exclusions. |
| `src/main/java/com/kingodogo/buildscape/util/ColorGradientSolver.java` | 91-95 | block | * * State a freshly placed workbench starts in: solid full blocks only, every other * category and both modifiers switched off, so the palette opens narrow and the * player widens it deliberately. |
| `src/main/java/com/kingodogo/buildscape/util/ColorGradientSolver.java` | 219-223 | block | * * Solves each interval between occupied anchor slots independently. Slots * outside the first and last anchor remain empty, and every anchor is copied * exactly into the output at its input position. |
| `src/main/java/com/kingodogo/buildscape/util/ColorGradientSolver.java` | 435 | line | Unknown custom shapes do not participate in Match Shape. |
| `src/main/java/com/kingodogo/buildscape/util/FestiveGlintHelper.java` | 17-19 | block | * * Checks if the given item stack has the festive enchantment glint applied. |
| `src/main/java/com/kingodogo/buildscape/util/FestiveGlintHelper.java` | 33-35 | block | * * Applies the festive enchantment glint to the item stack. |
| `src/main/java/com/kingodogo/buildscape/util/FestiveGlintHelper.java` | 45-47 | block | * * Checks if the given item stack is already enchanted (or has enchantment capability/foil). |
| `src/main/java/com/kingodogo/buildscape/world/ModGameRules.java` | 21 | line | Callback that triggers when either gamerule is changed on the server |
| `src/main/java/com/kingodogo/buildscape/world/ModGameRules.java` | 35 | line | Access the private create method with callback: create(boolean, BiConsumer) |
| `src/main/java/com/kingodogo/buildscape/world/ModGameRules.java` | 36 | line | m_46252_ is the SRG name for the overload taking a BiConsumer |
| `src/main/java/com/kingodogo/buildscape/world/ModGameRules.java` | 39 | line | Create the boolean value type with default value false and the sync callback |
| `src/main/java/com/kingodogo/buildscape/world/ModGameRules.java` | 43 | line | Register the gamerules with MISC category for visibility |
| `src/main/java/com/kingodogo/buildscape/worldgen/CreakingHeartTreeDecorator.java` | 30 | line | Guaranteed single Creaking Heart in the middle of the tree trunk |
| `src/test/java/com/kingodogo/buildscape/client/renderer/PipeSpillTest.java` | 10 | block | * Standalone geometry regression tests; does not launch Minecraft. Author: HoYin1600p. |
| `src/test/java/com/kingodogo/buildscape/client/renderer/PipeSpillTest.java` | 24 | line | Bottom surface. |
| `src/test/java/com/kingodogo/buildscape/client/renderer/PipeSpillTest.java` | 25 | line | Receiving water already higher than the outlet. |
| `src/test/java/com/kingodogo/buildscape/pipe/transport/PipeOutletWaterTest.java` | 5 | block | * Standalone outlet-supply regression tests. Author: HoYin1600p. |
| `build.gradle` | 111 | line | JEI |
| `build.gradle` | 112 | line | JEI Backup |
| `build.gradle` | 113 | line | JEI Backup |
| `build.gradle` | 125 | line | Optional renderer adapter only; Embeddium is neither bundled nor required at runtime. |
| `build.gradle` | 156 | line | Temporarily disabled for testing |
