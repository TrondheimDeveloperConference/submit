module Model.Submission exposing (Model, initModel, Submission, Speaker, Comment, initSpeaker)

import Backend.Network exposing (RequestStatus(..))
import Time


type alias Model =
    { submission : RequestStatus Submission
    , lastSaved : Maybe Time.Time
    , dirty : Bool
    , autosave : Bool
    , comment : String
    }


type alias Submission =
    { abstract : String
    , conferenceId : String
    , equipment : String
    , format : String
    , id : String
    , intendedAudience : String
    , language : String
    , length : String
    , outline : String
    , speakers : List ( Int, Speaker )
    , status : String
    , title : String
    , level : String
    , suggestedKeywords : String
    , infoToProgramCommittee : String
    , editable : Bool
    , status : String
    , comments : List Comment
    }


type alias Speaker =
    { bio : String
    , email : String
    , id : String
    , name : String
    , zipCode : String
    , twitter : String
    , deletable : Bool
    , hasPicture : Bool
    , pictureUrl : String
    , hasEmail : Bool
    }


type alias Comment =
    { name : String
    , comment : String
    }


initModel : Model
initModel =
    Model Initial Nothing False True ""


initSpeaker : List ( Int, Speaker ) -> ( Int, Speaker )
initSpeaker speakers =
    let
        nextInt =
            case List.head (List.reverse speakers) of
                Just ( i, s ) ->
                    i + 1

                _ ->
                    0
    in
        ( nextInt, Speaker "" "" "" "" "" "" True False "" False)


initComment : List ( Int, Comment ) -> ( Int, Comment )
initComment comments =
    let
        nextInt =
            case List.head (List.reverse comments) of
                Just ( i, s ) ->
                    i + 1

                _ ->
                    0
    in
        ( nextInt, Comment "" "" )
