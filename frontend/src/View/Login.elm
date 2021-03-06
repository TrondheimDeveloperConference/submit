module View.Login exposing (..)

import Html exposing (..)
import Html.Attributes exposing (class, src, type_, id, placeholder, value, disabled)
import Html.Events exposing (on, onClick, onInput, keyCode)
import Model.Login exposing (Model)
import Messages exposing (Msg(..))
import Json.Decode


view : Model -> Html Msg
view model =
    div [ class "wrapper login" ]
        [ div [ class "logo-wrapper" ]
            [ img [ src "assets/tdc-logo.svg", class "logo" ] [] ]
        , h1 [] [ text "Get ready to speak at", br [] [], text "Trondheim Developer Conference 2020" ]
        , div [ class "email-wrapper" ]
            [ input [ value model.email, onInput LoginEmail, onEnter LoginSubmitEmail, type_ "email", class "email", id "email-address", placeholder "Your email address" ] []
            , if model.loading then
                div [ class "spinner" ]
                    [ div [ class "bounce1" ] []
                    , div [ class "bounce2" ] []
                    , div [ class "bounce3" ] []
                    ]
              else
                button [ class "submit", type_ "submit", onClick LoginSubmitEmail, disabled model.loading ] []
            ]
        , div [ class "explanation" ]
            [ div [ class "arrow" ] []
            , div [ class "text" ]
                [ div [] [ text "We’ll email you a unique login link." ]
                , div [] [ text "Then you can start creating your talk." ]
                ]
            ]
        ]


onEnter : msg -> Attribute msg
onEnter msg =
    let
        isEnter code =
            if code == 13 then
                Json.Decode.succeed msg
            else
                Json.Decode.fail "not ENTER"
    in
        on "keydown" (Json.Decode.andThen isEnter keyCode)
