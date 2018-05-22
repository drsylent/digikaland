package hu.bme.aut.digikaland.dblogic.enumeration;

/**
 * Ha hiba történik a letöltések közben, akkor a hiba típusát ezzel az enummal lehet továbbítani
 * a UI logika felé.
 */
public enum ErrorType {
    NoContact,
    RaceNotExists,
    RoleNotExists,
    DatabaseError,
    EmptyField,
    IllegalCharacter,
    UploadError,
    DownloadError,
    PictureUploadError;

    /**
     * Egy default üzenet visszaadása minden hibatípushoz.
     * @return Egy általános üzenet a hibatípushoz tartozóan.
     */
    public String getDefaultMessage(){
        switch (this){
            case DatabaseError: return "Nem várt hiba az adatbázisban.";
            case NoContact: return "Nincs kapcsolat az adatbázissal.";
            case RaceNotExists: return "A megadott versenynév nem létezik.";
            case RoleNotExists: return "A megadott szerepkód nem helyes.";
            case IllegalCharacter: return "Helytelen karakter található az inputban.";
            case EmptyField: return "Kihagytál egy kötelező mezőt.";
            case UploadError: return "Feltöltés közben hiba történt.";
            case DownloadError: return "Letöltés közben hiba történt.";
            case PictureUploadError: return "Kép feltöltése közben hiba történt.";
            default: return "NONDEFINEDERROR";
        }
    }
}
