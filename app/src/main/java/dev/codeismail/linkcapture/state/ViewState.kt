package dev.codeismail.linkcapture.state

import dev.codeismail.linkcapture.adapter.Link

sealed class ViewState

object Loading : ViewState()

data class Success(val links : List<Link>): ViewState()