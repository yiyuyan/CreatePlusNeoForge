package org.xiyu.yee.createplus.mixin;

        import net.minecraft.client.multiplayer.ClientLevel;
        import net.minecraft.world.entity.Entity;
        import net.minecraft.world.level.Level;
        import org.spongepowered.asm.mixin.Mixin;
        import org.spongepowered.asm.mixin.injection.At;
        import org.spongepowered.asm.mixin.injection.Inject;
        import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
        import org.xiyu.yee.createplus.Createplus;
        import org.xiyu.yee.createplus.features.Performance;

        import java.util.List;

        @Mixin(ClientLevel.class)
        public abstract class ClientLevelMixin extends Level {
            protected ClientLevelMixin() {
                super(null, null, null, null, null, false, false, 0L, 0);
            }

            @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
            private void onTickNonPassenger(Entity entity, CallbackInfo ci) {
                // Here we can access and process the entity
                if (entity != null) {
                    List<Entity> entities = this.getEntities(entity, entity.getBoundingBox().inflate(32.0D));
                    if (entities.size() > 50) { // MAX_ENTITIES_PER_TYPE
                        entity.discard();
                        ci.cancel();
                    }
                }
            }

            @Inject(method = "addParticle", at = @At("HEAD"), cancellable = true)
            private void onAddParticle(CallbackInfo ci) {
                Performance performance = (Performance) Createplus.FEATURE_MANAGER.getFeature("性能优化");
                if (performance != null && performance.isEnabled() && performance.isDisableParticles()) {
                    ci.cancel();
                }
            }
        }