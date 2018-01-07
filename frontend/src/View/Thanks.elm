module View.Thanks exposing (..)

import Html exposing (..)
import Html.Attributes exposing (class, src)
import Messages exposing (Msg(..))


view : Html Msg
view =
    div [ class "wrapper login " ]
        [ div [ class "logo-wrapper" ]
            [ img [ src "assets/tdc-logo.svg", class "logo" ] []
            ]
        , h1 [] [ text "Awesome!", br [] [], text " We are on it!" ]
        , div [ class "success-image-wrapper" ]
            [ img [ src "/assets/plane2.png", class "success-image" ] []
            ]
        , div [ class "email-success" ]
            [ div [] [ text "Please check your email." ]
            , div [] [ text "Click the link to get started." ]
            ]
        ]
