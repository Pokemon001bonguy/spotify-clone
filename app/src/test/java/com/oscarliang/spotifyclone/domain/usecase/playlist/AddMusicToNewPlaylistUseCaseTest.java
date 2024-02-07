package com.oscarliang.spotifyclone.domain.usecase.playlist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.oscarliang.spotifyclone.domain.model.Music;
import com.oscarliang.spotifyclone.domain.model.Playlist;
import com.oscarliang.spotifyclone.domain.repository.PlaylistRepository;
import com.oscarliang.spotifyclone.util.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

@RunWith(JUnit4.class)
public class AddMusicToNewPlaylistUseCaseTest {

    private PlaylistRepository mRepository;
    private AddMusicToNewPlaylistUseCase mUseCase;

    @Before
    public void init() {
        mRepository = mock(PlaylistRepository.class);
        mUseCase = new AddMusicToNewPlaylistUseCase(mRepository);
    }

    @Test
    public void execute() {
        mUseCase.execute("foo", "bar", TestUtil.createMusic("abc", "cba"));
        // The default playlist id is null
        // Because playlist id will be auto generated by firestore
        verify(mRepository).addPlaylist("foo",
                TestUtil.createPlaylist(null, "bar", "cba", Collections.singletonList("abc")));
    }

}