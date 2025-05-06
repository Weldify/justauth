package weldify.justauth.mixin;

import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import weldify.justauth.JustAuth;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Mixin(value = PacketHandlerLogin.class)
public class PacketHandlerLoginMixin {
	@Inject(method = "doLogin", at = @At("HEAD"), cancellable = true, remap = false)
	void doLogin(PacketLogin packet, CallbackInfo ci) {
		PacketHandlerLogin thisObject = (PacketHandlerLogin) (Object) this;

		byte[] providedPassword = JustAuth.serverReadPasswords.get(packet);
		JustAuth.serverReadPasswords.remove(packet);

		try {
			byte[] requiredPassword = Files.readAllBytes(Paths.get("justauth/" + packet.username));
			if (Arrays.equals(providedPassword, requiredPassword)) return;
		} catch (Exception ignored) {
		}

		thisObject.kickUser("");
		ci.cancel();
	}
}
