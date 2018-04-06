package hu.bme.aut.digikaland.dblogic;

public enum ErrorType {
    NoContact,
    RaceNotExists,
    RoleNotExists,
    DatabaseError,
    EmptyField,
    IllegalCharacter;

    public String getDefaultMessage(){
        switch (this){
            case DatabaseError: return "Nem várt hiba az adatbázisban.";
            case NoContact: return "Nincs kapcsolat az adatbázissal.";
            case RaceNotExists: return "A megadott versenynév nem létezik.";
            case RoleNotExists: return "A megadott szerepkód nem helyes.";
            case IllegalCharacter: return "Helytelen karakter található az inputban.";
            case EmptyField: return "Kihagytál egy kötelező mezőt.";
            default: return "NONDEFINEDERROR";
        }
    }
}
