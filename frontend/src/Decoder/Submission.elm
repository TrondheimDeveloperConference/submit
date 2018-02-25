module Decoder.Submission exposing (decoder, decodeComment)

import Model.Submission exposing (..)
import Json.Decode exposing (Decoder, string, list, map, bool)
import Json.Decode.Pipeline exposing (decode, required, optional)


decoder : Decoder Submission
decoder =
    decode Submission
        |> optional "abstract" string ""
        |> required "conferenceId" string
        |> optional "equipment" string ""
        |> optional "format" string ""
        |> required "id" string
        |> optional "intendedAudience" string ""
        |> optional "language" string ""
        |> optional "length" string "60"
        |> optional "outline" string ""
        |> required "speakers" (map speakersToTuples <| list decodeSpeaker)
        |> required "status" string
        |> optional "title" string ""
        |> optional "level" string "beginner"
        |> optional "suggestedKeywords" string ""
        |> optional "infoToProgramCommittee" string ""
        |> required "editable" bool
        |> required "status" string
        |> required "comments" (list decodeComment)


speakersToTuples : List Speaker -> List ( Int, Speaker )
speakersToTuples speakers =
    List.map2 (,) (List.range 0 (List.length speakers)) speakers


decodeSpeaker : Decoder Speaker
decodeSpeaker =
    decode Speaker
        |> optional "bio" string ""
        |> optional "email" string ""
        |> required "id" string
        |> optional "name" string ""
        |> optional "zipCode" string ""
        |> optional "twitter" string ""
        |> required "deletable" bool
        |> required "hasPicture" bool
        |> optional "pictureUrl" string ""


decodeComment : Decoder Comment
decodeComment =
    decode Comment
        |> optional "name" string ""
        |> optional "comment" string ""
