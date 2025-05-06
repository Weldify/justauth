package justauth;

import net.minecraft.core.net.packet.PacketLogin;

import java.util.HashMap;

public class JustAuth {
	public static HashMap<PacketLogin, byte[]> serverReadPasswords = new HashMap<>();
}
