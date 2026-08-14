package gg.moonflower.etched.core;

/** Loader-neutral settings used until a Fabric configuration screen is added. */
public final class EtchedConfig {
    private EtchedConfig() {
    }

    public static final class BooleanOption {
        private boolean value;

        public BooleanOption(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean value) {
            this.value = value;
        }
    }

    public static final class Client {
        public final BooleanOption forceStereo = new BooleanOption(false);
        public final BooleanOption smoothParrotAnimation = new BooleanOption(true);
    }

    public static final class Server {
        public final BooleanOption useBoomboxMenu = new BooleanOption(false);
        public final BooleanOption useAlbumCoverMenu = new BooleanOption(false);
    }
}
