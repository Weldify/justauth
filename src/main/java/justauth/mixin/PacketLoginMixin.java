package justauth.mixin;

import net.minecraft.core.Global;
import net.minecraft.core.net.packet.PacketLogin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import justauth.JustAuth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Mixin(value = PacketLogin.class)
public class PacketLoginMixin {
	@Inject(method = "read", at = @At("HEAD"), remap = false)
	void read(DataInputStream dis, CallbackInfo ci) throws IOException {
		int length = dis.readByte();
		byte[] password = new byte[length];
		dis.readFully(password);

		if (Global.isServer) {
			PacketLogin thisObject = (PacketLogin) (Object) this;
			JustAuth.serverReadPasswords.put(thisObject, password);
			// ^ Filled out for PacketHandlerLoginMixin, which runs right after the packet is decoded.
		}
	}

	@Inject(method = "write", at = @At("HEAD"), remap = false)
	void write(DataOutputStream dos, CallbackInfo ci) {
		try {
			byte[] password = Files.readAllBytes(Paths.get("just.auth"));
			dos.writeByte(password.length);
			dos.write(password);
		} catch (Exception ignored) {
			// Don't send anything! Might let people join regular servers.
		}
	}
}
